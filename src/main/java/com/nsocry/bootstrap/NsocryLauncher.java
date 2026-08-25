package com.nsocry.bootstrap;

import java.nio.file.Path;

/** Entry point duy nhất của executable JAR, phân luồng lệnh vận hành rõ ràng. */
public final class NsocryLauncher {
    private NsocryLauncher() {
    }

    /** Phân tích argument rồi chạy server, tạo administrator hoặc in trợ giúp. */
    public static void main(String[] args) throws Exception {
        LaunchRequest request = parse(args);
        String[] forwarded = request.configurationPath() == null
                ? new String[0]
                : new String[] {request.configurationPath().toString()};
        switch (request.command()) {
            case SERVER -> NsocryServerApplication.main(forwarded);
            case CREATE_ADMIN -> FirstAdministratorCommand.main(forwarded);
            case ITEM_SEED_CONVERT -> ItemAssetSeedConvertCommand.main(forwarded);
            case ITEM_SEED_DRY_RUN -> ItemAssetSeedDryRunCommand.main(forwarded);
            case ITEM_SEED_IMPORT -> ItemAssetSeedImportCommand.main(forwarded);
            case ITEM_SEED_DB_VERIFY -> ItemAssetDatabaseVerifyCommand.main(forwarded);
            case ITEM_SCHEMA_PREFLIGHT -> ItemAssetSchemaPreflightCommand.main(forwarded);
            case SKILL_SEED_CONVERT -> SkillAssetSeedConvertCommand.main(forwarded);
            case SKILL_SEED_DRY_RUN -> SkillAssetSeedDryRunCommand.main(forwarded);
            case HELP -> printUsage();
        }
    }

    /** Chấp nhận đúng một command và tối đa một đường dẫn config. */
    static LaunchRequest parse(String[] args) {
        if (args == null || args.length == 0) {
            return new LaunchRequest(LaunchCommand.HELP, null);
        }
        if (args.length > 2) {
            throw new IllegalArgumentException("expected command and optional configuration path");
        }
        LaunchCommand command = switch (args[0]) {
            case "server" -> LaunchCommand.SERVER;
            case "create-admin" -> LaunchCommand.CREATE_ADMIN;
            case "item-seed-convert" -> LaunchCommand.ITEM_SEED_CONVERT;
            case "item-seed-dry-run" -> LaunchCommand.ITEM_SEED_DRY_RUN;
            case "item-seed-import" -> LaunchCommand.ITEM_SEED_IMPORT;
            case "item-seed-db-verify" -> LaunchCommand.ITEM_SEED_DB_VERIFY;
            case "item-schema-preflight" -> LaunchCommand.ITEM_SCHEMA_PREFLIGHT;
            case "skill-seed-convert" -> LaunchCommand.SKILL_SEED_CONVERT;
            case "skill-seed-dry-run" -> LaunchCommand.SKILL_SEED_DRY_RUN;
            case "help", "--help", "-h" -> LaunchCommand.HELP;
            default -> throw new IllegalArgumentException("unknown NSOCry command: " + args[0]);
        };
        if (command == LaunchCommand.HELP && args.length == 2) {
            throw new IllegalArgumentException("help command does not accept a configuration path");
        }
        return new LaunchRequest(command, args.length == 2 ? Path.of(args[1]) : null);
    }

    /** In cú pháp vận hành, không in hoặc yêu cầu credential. */
    private static void printUsage() {
        System.out.println("NSOCry commands:");
        System.out.println("  java -jar nsocry-server.jar server [config-path]");
        System.out.println("  java -jar nsocry-server.jar create-admin [config-path]");
        System.out.println("  java -jar nsocry-server.jar item-seed-convert <dump-path>");
        System.out.println("  java -jar nsocry-server.jar item-seed-dry-run <archive-path>");
        System.out.println("  java -jar nsocry-server.jar item-seed-import <archive-path>");
        System.out.println("  java -jar nsocry-server.jar item-seed-db-verify <archive-path>");
        System.out.println("  java -jar nsocry-server.jar item-schema-preflight [config-path]");
        System.out.println("  java -jar nsocry-server.jar skill-seed-convert <dump-path>");
        System.out.println("  java -jar nsocry-server.jar skill-seed-dry-run <archive-path>");
        System.out.println("  java -jar nsocry-server.jar help");
    }

    /** Các command hợp lệ của executable JAR. */
    enum LaunchCommand {
        SERVER,
        CREATE_ADMIN,
        ITEM_SEED_CONVERT,
        ITEM_SEED_DRY_RUN,
        ITEM_SEED_IMPORT,
        ITEM_SEED_DB_VERIFY,
        ITEM_SCHEMA_PREFLIGHT,
        SKILL_SEED_CONVERT,
        SKILL_SEED_DRY_RUN,
        HELP
    }

    /** Kết quả parse command cùng đường dẫn config tùy chọn. */
    record LaunchRequest(LaunchCommand command, Path configurationPath) {
    }
}
