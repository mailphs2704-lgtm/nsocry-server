package com.nsocry.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Gate buộc source production và phần chưa đủ dữ liệu luôn truy vấn được trong manual. */
class DocumentationCoverageTest {
    private static final Path PRODUCTION = Path.of("src", "main", "java");
    private static final Path MANUAL = Path.of("docs", "developer-manual");

    @Test
    void codeCatalogContainsEveryProductionJavaSource() throws Exception {
        String catalog = Files.readString(MANUAL.resolve("code-catalog.md"));
        try (var paths = Files.walk(PRODUCTION)) {
            for (Path source : paths.filter(path -> path.toString().endsWith(".java")).toList()) {
                String normalized = source.toString().replace('\\', '/');
                assertTrue(catalog.contains("`" + normalized + "`"),
                        "Code catalog thiếu source: " + normalized);
            }
        }
    }

    @Test
    void managementManualContainsRequiredQueriesAndTraceMarkers() throws Exception {
        List<String> requiredFiles = List.of(
                "README.md", "architecture-and-flows.md", "change-playbooks.md",
                "operations-troubleshooting.md", "trace-register.md", "maintenance-standard.md");
        for (String name : requiredFiles) {
            assertTrue(Files.isRegularFile(MANUAL.resolve(name)), "Thiếu developer manual: " + name);
        }
        String index = Files.readString(MANUAL.resolve("README.md"));
        for (String concept : List.of("Chức năng", "Vai trò", "Đầu vào/đầu ra", "Luồng gọi",
                "Bất biến", "Lỗi", "Cách sửa", "Độ tin cậy")) {
            assertTrue(index.contains(concept), "Manual thiếu trường quản trị: " + concept);
        }
        String trace = Files.readString(MANUAL.resolve("trace-register.md"));
        assertTrue(trace.contains("TRACE_REQUIRED"));
        assertTrue(trace.contains("Điều kiện đóng truy vết"));
    }
}
