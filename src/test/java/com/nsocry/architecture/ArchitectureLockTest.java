package com.nsocry.architecture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Gate bắt buộc để AI sau không tự ý phá package/naming/dependency đã khóa. */
class ArchitectureLockTest {
    private static final Path MANIFEST = Path.of("docs", "architecture", "planned-contracts.tsv");
    private static final Path PRODUCTION = Path.of("src", "main", "java");

    @Test
    void contractManifestHasValidUniqueRows() throws Exception {
        List<String[]> rows = contracts();
        Set<String> identities = new HashSet<>();
        assertFalse(rows.isEmpty());
        for (String[] row : rows) {
            assertTrue(Set.of("LOCKED", "RESERVED", "REFERENCE_ONLY").contains(row[0]));
            assertTrue(row[1].startsWith("com.nsocry."));
            assertFalse(row[2].isBlank());
            assertFalse(row[4].isBlank());
            assertFalse(row[5].isBlank());
            assertTrue(identities.add(row[1] + "." + row[2]), "Contract bị trùng: " + row[1] + "." + row[2]);
        }
    }

    @Test
    void everyProductionPackageIsDeclaredByArchitectureLock() throws Exception {
        Set<String> declared = new HashSet<>();
        for (String[] row : contracts()) declared.add(row[1]);
        for (Path source : javaSources(PRODUCTION)) {
            String packageName = packageName(Files.readString(source));
            assertTrue(declared.contains(packageName), "Package chưa khai báo trong architecture lock: " + packageName);
        }
    }

    @Test
    void productionAndTestsNeverDeclareOrImportLegacyNamespace() throws Exception {
        for (Path root : List.of(PRODUCTION, Path.of("src", "test", "java"))) {
            for (Path source : javaSources(root)) {
                String text = Files.readString(source);
                for (String line : text.lines().toList()) {
                    String trimmed = line.strip();
                    if (trimmed.startsWith("package ") || trimmed.startsWith("import ")) {
                        assertFalse(trimmed.contains("com.nsoz") || trimmed.contains("com.nsotien"),
                                "Namespace cũ xuất hiện trong " + source);
                    }
                }
            }
        }
    }

    @Test
    void gameplayDomainDoesNotDependOnAdaptersTransportOrBootstrap() throws Exception {
        List<String> forbidden = List.of("com.nsocry.persistence", "com.nsocry.operations",
                "com.nsocry.bootstrap", "com.nsocry.network", "java.sql", "javax.sql");
        Path gameRoot = PRODUCTION.resolve(Path.of("com", "nsocry", "game"));
        for (Path source : javaSources(gameRoot)) {
            String text = Files.readString(source);
            for (String dependency : forbidden) {
                assertFalse(text.contains("import " + dependency),
                        "Gameplay domain phụ thuộc adapter/transport: " + source + " -> " + dependency);
            }
        }
    }

    @Test
    void agentRulesRequireVietnameseCheckpointAndArchitectureChangeControl() throws Exception {
        String rules = Files.readString(Path.of("AGENTS.md"));
        assertTrue(rules.contains("STATUS.md"));
        assertTrue(rules.contains("WORKLOG.md"));
        assertTrue(rules.contains("tiếng Việt"));
        assertTrue(rules.contains("Architecture Decision Record"));
        assertTrue(Files.readString(Path.of("docs", "architecture", "architecture-lock.md"))
                .contains("Quy trình thay đổi khung"));
    }

    @Test
    void statusContainsExactlyOneCurrentActionAndReviewCannotBeSelfDeclared() throws Exception {
        String status = Files.readString(Path.of("docs", "project", "STATUS.md"));
        assertTrue(status.split("## Next exact action", -1).length == 2,
                "STATUS phải có đúng một Next exact action hiện tại");
        String rules = Files.readString(Path.of("AGENTS.md"));
        assertTrue(rules.contains("Cấm tự công bố kết quả rà soát"));
        assertTrue(rules.contains("reviewer độc lập"));
    }

    private static List<String[]> contracts() throws IOException {
        List<String[]> rows = new ArrayList<>();
        List<String> lines = Files.readAllLines(MANIFEST);
        assertFalse(lines.isEmpty());
        assertTrue(lines.get(0).equals("status\tpackage\ttype\tkind\tresponsibility\trequired_methods"));
        for (int index = 1; index < lines.size(); index++) {
            if (lines.get(index).isBlank()) continue;
            String[] columns = lines.get(index).split("\\t", -1);
            assertTrue(columns.length == 6, "Dòng TSV phải có đúng 6 cột: " + (index + 1));
            rows.add(columns);
        }
        return rows;
    }

    private static List<Path> javaSources(Path root) throws IOException {
        if (!Files.isDirectory(root)) return List.of();
        try (var paths = Files.walk(root)) {
            return paths.filter(path -> path.toString().endsWith(".java")).sorted().toList();
        }
    }

    private static String packageName(String source) {
        return source.lines().map(String::strip)
                .filter(line -> line.startsWith("package "))
                .map(line -> line.substring("package ".length(), line.length() - 1))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Source thiếu package declaration"));
    }
}
