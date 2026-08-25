package com.nsocry.persistence;

import com.nsocry.assets.SkillAssetBundle;
import com.nsocry.assets.SkillAssetCodec;
import com.nsocry.assets.SkillAssetSeedManifest;
import com.nsocry.assets.SkillAssetSeedManifestParser;
import com.nsocry.assets.SkillAssetSeedValidationResult;
import com.nsocry.assets.SkillAssetSeedValidator;
import com.nsocry.assets.SkillClassAsset;
import com.nsocry.assets.SkillLevelAsset;
import com.nsocry.assets.SkillLevelOptionAsset;
import com.nsocry.assets.SkillTemplateAsset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.sql.DataSource;

/** Thay toàn bộ SKILL seed đã kiểm định trong một transaction SERIALIZABLE. */
public final class JdbcSkillAssetSeedImporter {
    private static final String[] DELETE_SQL = {
            "DELETE FROM client_skill_level_options", "DELETE FROM client_skill_levels",
            "DELETE FROM client_skill_templates", "DELETE FROM client_skill_classes",
            "DELETE FROM client_skill_options"};
    private static final String INSERT_OPTION = "INSERT INTO client_skill_options (id, name) VALUES (?, ?)";
    private static final String INSERT_CLASS = "INSERT INTO client_skill_classes (id, name) VALUES (?, ?)";
    private static final String INSERT_TEMPLATE = """
            INSERT INTO client_skill_templates
                (id, class_id, sort_order, name, max_point, type, icon_id, description)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String INSERT_LEVEL = """
            INSERT INTO client_skill_levels
                (id, template_id, sort_order, point, required_level, mana_use, cooldown, dx, dy, max_fight)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String INSERT_LEVEL_OPTION = """
            INSERT INTO client_skill_level_options
                (skill_level_id, sort_order, parameter_value, option_template_id)
            VALUES (?, ?, ?, ?)
            """;

    private final DataSource dataSource;

    public JdbcSkillAssetSeedImporter(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    /** Validate trước khi mở connection; commit đủ năm bảng hoặc rollback toàn bộ. */
    public SkillAssetSeedValidationResult importSeed(byte[] payload, String manifestText)
            throws SkillAssetSeedImportException {
        Objects.requireNonNull(payload, "payload");
        Objects.requireNonNull(manifestText, "manifestText");
        final SkillAssetBundle bundle;
        final SkillAssetSeedValidationResult validation;
        try {
            SkillAssetSeedManifest manifest = SkillAssetSeedManifestParser.parse(manifestText);
            bundle = SkillAssetCodec.decode(payload.clone());
            validation = SkillAssetSeedValidator.validate(bundle, manifest);
            if (payload.length != validation.payloadLength()) throw new IllegalArgumentException("Sai payload length");
        } catch (Exception exception) {
            throw new SkillAssetSeedImportException("SKILL artifact không hợp lệ", exception);
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);
            try {
                replaceRows(connection, bundle);
                connection.commit();
                return validation;
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException | RuntimeException exception) {
            throw new SkillAssetSeedImportException("Không thể ghi SKILL seed", exception);
        }
    }

    private static void replaceRows(Connection connection, SkillAssetBundle bundle) throws SQLException {
        for (String sql : DELETE_SQL) try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
        insertOptions(connection, bundle);
        insertClasses(connection, bundle);
        insertTemplates(connection, bundle);
        insertLevels(connection, bundle);
        insertLevelOptions(connection, bundle);
    }

    private static void insertOptions(Connection connection, SkillAssetBundle bundle) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_OPTION)) {
            for (int id = 0; id < bundle.optionTemplateNames().size(); id++) {
                statement.setInt(1, id); statement.setString(2, bundle.optionTemplateNames().get(id)); statement.addBatch();
            }
            requireBatch(statement.executeBatch(), bundle.optionTemplateNames().size(), "skill options");
        }
    }

    private static void insertClasses(Connection connection, SkillAssetBundle bundle) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_CLASS)) {
            for (int id = 0; id < bundle.classes().size(); id++) {
                statement.setInt(1, id); statement.setString(2, bundle.classes().get(id).name()); statement.addBatch();
            }
            requireBatch(statement.executeBatch(), bundle.classes().size(), "skill classes");
        }
    }

    private static void insertTemplates(Connection connection, SkillAssetBundle bundle) throws SQLException {
        int expected = 0;
        try (PreparedStatement statement = connection.prepareStatement(INSERT_TEMPLATE)) {
            for (int classId = 0; classId < bundle.classes().size(); classId++) {
                SkillClassAsset skillClass = bundle.classes().get(classId);
                for (int order = 0; order < skillClass.templates().size(); order++) {
                    SkillTemplateAsset template = skillClass.templates().get(order);
                    statement.setInt(1, Byte.toUnsignedInt(template.id())); statement.setInt(2, classId);
                    statement.setInt(3, order); statement.setString(4, template.name());
                    statement.setByte(5, template.maxPoint()); statement.setByte(6, template.type());
                    statement.setShort(7, template.icon()); statement.setString(8, template.description());
                    statement.addBatch(); expected++;
                }
            }
            requireBatch(statement.executeBatch(), expected, "skill templates");
        }
    }

    private static void insertLevels(Connection connection, SkillAssetBundle bundle) throws SQLException {
        int expected = 0;
        try (PreparedStatement statement = connection.prepareStatement(INSERT_LEVEL)) {
            for (SkillClassAsset skillClass : bundle.classes()) for (SkillTemplateAsset template : skillClass.templates()) {
                for (int order = 0; order < template.levels().size(); order++) {
                    SkillLevelAsset level = template.levels().get(order);
                    statement.setInt(1, level.id()); statement.setInt(2, Byte.toUnsignedInt(template.id()));
                    statement.setInt(3, order); statement.setInt(4, Byte.toUnsignedInt(level.point()));
                    statement.setByte(5, level.requiredLevel()); statement.setShort(6, level.manaUse());
                    statement.setInt(7, level.coolDown()); statement.setShort(8, level.dx());
                    statement.setShort(9, level.dy()); statement.setByte(10, level.maxFight());
                    statement.addBatch(); expected++;
                }
            }
            requireBatch(statement.executeBatch(), expected, "skill levels");
        }
    }

    private static void insertLevelOptions(Connection connection, SkillAssetBundle bundle) throws SQLException {
        int expected = 0;
        try (PreparedStatement statement = connection.prepareStatement(INSERT_LEVEL_OPTION)) {
            for (SkillClassAsset skillClass : bundle.classes()) for (SkillTemplateAsset template : skillClass.templates())
                for (SkillLevelAsset level : template.levels()) for (int order = 0; order < level.options().size(); order++) {
                    SkillLevelOptionAsset option = level.options().get(order);
                    statement.setInt(1, level.id()); statement.setInt(2, order);
                    statement.setShort(3, option.parameter());
                    statement.setInt(4, Byte.toUnsignedInt(option.optionTemplateId()));
                    statement.addBatch(); expected++;
                }
            requireBatch(statement.executeBatch(), expected, "skill level options");
        }
    }

    private static void requireBatch(int[] results, int expected, String name) throws SQLException {
        if (results.length != expected) throw new SQLException(name + " batch count không khớp");
        for (int result : results) if (result == Statement.EXECUTE_FAILED) throw new SQLException(name + " batch thất bại");
    }

    private static void rollback(Connection connection, Exception original) {
        try { connection.rollback(); } catch (SQLException rollbackFailure) { original.addSuppressed(rollbackFailure); }
    }
}
