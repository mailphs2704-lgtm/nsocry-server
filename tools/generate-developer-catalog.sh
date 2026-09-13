#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source_root="$repo_root/src/main/java"
target="$repo_root/docs/developer-manual/code-catalog.md"
temporary="${target}.tmp"

package_role() {
  case "$1" in
    com.nsocry.assets) echo "Read model, codec, manifest, validator và runtime asset snapshot." ;;
    com.nsocry.assets.conversion) echo "Parse/chuyển nguồn reference offline sang immutable asset bundle." ;;
    com.nsocry.authentication) echo "Credential policy và application authentication contract." ;;
    com.nsocry.bootstrap) echo "Composition root, launcher và command vận hành." ;;
    com.nsocry.character) echo "Read model/payload danh sách nhân vật sau đăng nhập." ;;
    com.nsocry.configuration) echo "Load và validate cấu hình server/database." ;;
    com.nsocry.network) echo "TCP acceptor, connection lifecycle và network event boundary." ;;
    com.nsocry.observability) echo "Log/sự kiện đã làm sạch dữ liệu nhạy cảm." ;;
    com.nsocry.operations) echo "Use-case vận hành archive/migration/import có safety gate." ;;
    com.nsocry.persistence) echo "JDBC adapter, schema inspector, repository/source/importer." ;;
    com.nsocry.protocol.compat) echo "Wire frame, key transform, payload reader/writer và client compatibility." ;;
    com.nsocry.session) echo "Handshake/login phase, processor và session transport." ;;
    *) echo "TRACE_REQUIRED: package chưa có mô tả trách nhiệm." ;;
  esac
}

first_javadoc_summary() {
  awk '
    /\/\*\*/ {
      line=$0
      sub(/^.*\/\*\*[[:space:]]*/, "", line)
      sub(/[[:space:]]*\*\/.*$/, "", line)
      if (line != "" && line !~ /^@/) {print line; exit}
      in_doc=1
      next
    }
    in_doc {
      line=$0
      sub(/^[[:space:]]*\*[[:space:]]?/, "", line)
      sub(/[[:space:]]*\*\/$/, "", line)
      if (line != "" && line !~ /^@/) {print line; exit}
      if ($0 ~ /\*\//) exit
    }
  ' "$1"
}

documented_declarations() {
  awk '
    function clean(text) {
      sub(/^[[:space:]]*\*[[:space:]]?/, "", text)
      sub(/[[:space:]]*\*\/.*$/, "", text)
      return text
    }
    /\/\*\*/ {
      line=$0
      sub(/^.*\/\*\*[[:space:]]*/, "", line)
      sub(/[[:space:]]*\*\/.*$/, "", line)
      doc=line
      if ($0 !~ /\*\//) in_doc=1
      next
    }
    in_doc {
      line=clean($0)
      if (doc == "" && line != "" && line !~ /^@/) doc=line
      if ($0 ~ /\*\//) in_doc=0
      next
    }
    /^[[:space:]]*(public|protected|private|static|final|synchronized)/ {
      if (($0 ~ /\(/ || $0 ~ /[[:space:]](class|record|interface|enum)[[:space:]]/) && $0 !~ /=/) {
        declaration=$0
        sub(/^[[:space:]]*/, "", declaration)
        gsub(/\t/, " ", declaration)
        if ($0 ~ /^[[:space:]]*(public|protected)/) {
          print NR "\t" declaration "\t" doc
        }
        doc=""
      }
    }
  ' "$1"
}

{
  printf '# Danh mục code production NSOCry\n\n'
  printf '> File này được sinh cơ học từ `src/main/java` bởi `tools/generate-developer-catalog.sh`.\n\n'
  printf '> Catalog giúp tìm symbol; mô tả hành vi chuẩn nằm trong manual module và STATUS.\n\n'
  printf '## Cách dùng\n\n'
  printf -- '- Tìm theo `package.Type`, tên method hoặc source path.\n'
  printf -- '- `IMPLEMENTED` chỉ xác nhận code tồn tại; xem STATUS/manual để biết mức VERIFIED.\n'
  printf -- '- Method package-private/private quan trọng có thể không xuất hiện trong danh sách API; xem source và manual module.\n'
  printf -- '- Khi thêm/xóa source phải sinh lại catalog và chạy `DocumentationCoverageTest`.\n\n'

  while IFS= read -r package_dir; do
    package_name="${package_dir#${source_root}/}"
    package_name="${package_name//\//.}"
    printf '## `%s`\n\n' "$package_name"
    printf '**Vai trò:** %s\n\n' "$(package_role "$package_name")"
    printf '**Trạng thái:** `IMPLEMENTED`; bằng chứng chi tiết xem [STATUS](../project/STATUS.md).\n\n'

    while IFS= read -r file; do
      relative="${file#${repo_root}/}"
      type_name="$(basename "$file" .java)"
      summary="$(first_javadoc_summary "$file")"
      if [[ -z "$summary" ]]; then
        summary="TRACE_REQUIRED: source chưa có Javadoc cấp type đủ để sinh mô tả."
      fi
      printf '### `%s`\n\n' "$package_name.$type_name"
      printf -- '- **Source:** `%s`\n' "$relative"
      printf -- '- **Vai trò tóm tắt:** %s\n' "$summary"
      printf -- '- **Trạng thái:** `IMPLEMENTED`\n'
      printf -- '- **API public/protected phát hiện được:**\n'
      declarations="$(documented_declarations "$file")"
      if [[ -z "$declarations" ]]; then
        printf '  - Không có API public/protected một dòng; xem source/package contract.\n'
      else
        while IFS=$'\t' read -r line_number declaration declaration_doc; do
          declaration="${declaration//|/\\|}"
          declaration_doc="${declaration_doc//|/\\|}"
          if [[ -z "$declaration_doc" ]]; then
            declaration_doc="TRACE_REQUIRED: declaration chưa có Javadoc gần nhất."
          fi
          printf '  - **Dòng %s — `%s`**: %s\n' "$line_number" "$declaration" "$declaration_doc"
        done <<< "$declarations"
      fi
      printf -- '- **Khi sửa:** kiểm tra caller bằng `rg`, cập nhật test + manual module + STATUS/WORKLOG; không đổi contract LOCKED nếu thiếu ADR.\n\n'
    done < <(find "$package_dir" -maxdepth 1 -name '*.java' -type f | sort)
  done < <(find "$source_root" -type d | while read -r directory; do
    find "$directory" -maxdepth 1 -name '*.java' -type f -print -quit | grep -q . && printf '%s\n' "$directory"
  done | sort)

  printf '## Phạm vi chưa có source\n\n'
  printf 'Các package gameplay RESERVED/TRACE_REQUIRED không được tạo stub chỉ để xuất hiện trong catalog. '
  printf 'Tra cứu [planned-contracts.tsv](../architecture/planned-contracts.tsv) và [trace-register](trace-register.md).\n'
} > "$temporary"

mv "$temporary" "$target"
