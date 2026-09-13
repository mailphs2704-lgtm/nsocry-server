package com.nsocry.persistence;

import com.nsocry.assets.ClientAssetSourceException;
import com.nsocry.assets.SkillAssetBundle;
import com.nsocry.assets.SkillAssetSource;
import com.nsocry.assets.SkillClassAsset;
import com.nsocry.assets.SkillLevelAsset;
import com.nsocry.assets.SkillLevelOptionAsset;
import com.nsocry.assets.SkillTemplateAsset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;

/** Adapter JDBC tái dựng toàn bộ SKILL read model trong một snapshot nhất quán. */
public final class JdbcSkillAssetSource implements SkillAssetSource {
    private static final String READ_OPTIONS = "SELECT id, name FROM client_skill_options ORDER BY id";
    private static final String READ_CLASSES = "SELECT id, name FROM client_skill_classes ORDER BY id";
    private static final String READ_TEMPLATES = """
            SELECT id, class_id, sort_order, name, max_point, type, icon_id, description
            FROM client_skill_templates ORDER BY id
            """;
    private static final String READ_LEVELS = """
            SELECT id, template_id, sort_order, point, required_level, mana_use, cooldown, dx, dy, max_fight
            FROM client_skill_levels ORDER BY id
            """;
    private static final String READ_LEVEL_OPTIONS = """
            SELECT skill_level_id, sort_order, parameter_value, option_template_id
            FROM client_skill_level_options ORDER BY skill_level_id, sort_order
            """;

    private final DataSource dataSource;
    private final byte version;

    /** Tạo source JDBC với version payload explicit; chưa đọc database ở constructor. */
    public JdbcSkillAssetSource(DataSource dataSource, byte version) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
        this.version = version;
    }

    /** Đọc năm bảng trên cùng repeatable-read transaction và commit snapshot chỉ đọc. */
    @Override
    public SkillAssetBundle load() throws ClientAssetSourceException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setReadOnly(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);
            try {
                List<String> options = readNames(connection, READ_OPTIONS, "client_skill_options");
                List<String> classNames = readNames(connection, READ_CLASSES, "client_skill_classes");
                Map<Integer, TemplateBuilder> templates = readTemplates(connection, classNames.size());
                Map<Integer, LevelBuilder> levels = readLevels(connection, templates);
                readLevelOptions(connection, levels, options.size());
                SkillAssetBundle bundle = build(version, options, classNames, templates);
                connection.commit();
                return bundle;
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException | RuntimeException exception) {
            throw new ClientAssetSourceException("Không thể đọc SKILL asset", exception);
        }
    }

    private static List<String> readNames(Connection connection, String sql, String table) throws SQLException {
        List<String> names = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                requireSequential(result.getInt("id"), names.size(), table);
                names.add(Objects.requireNonNull(result.getString("name"), table + " name"));
            }
        }
        return names;
    }

    private static Map<Integer, TemplateBuilder> readTemplates(Connection connection, int classCount) throws SQLException {
        Map<Integer, TemplateBuilder> templates = new LinkedHashMap<>();
        int[] nextOrder = new int[classCount];
        try (PreparedStatement statement = connection.prepareStatement(READ_TEMPLATES);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                int id = result.getInt("id");
                requireSequential(id, templates.size(), "client_skill_templates");
                int classId = result.getInt("class_id");
                requireIndex(classId, classCount, "template class");
                requireSequential(result.getInt("sort_order"), nextOrder[classId]++, "template sort_order");
                templates.put(id, new TemplateBuilder(id, classId,
                        Objects.requireNonNull(result.getString("name"), "template name"),
                        checkedSignedByte(result.getInt("max_point"), "max_point"),
                        checkedSignedByte(result.getInt("type"), "type"),
                        checkedShort(result.getInt("icon_id"), "icon_id"),
                        Objects.requireNonNull(result.getString("description"), "template description")));
            }
        }
        return templates;
    }

    private static Map<Integer, LevelBuilder> readLevels(
            Connection connection, Map<Integer, TemplateBuilder> templates) throws SQLException {
        Map<Integer, LevelBuilder> levels = new LinkedHashMap<>();
        Map<Integer, Integer> nextOrder = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(READ_LEVELS);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                int id = result.getInt("id");
                requireSequential(id, levels.size(), "client_skill_levels");
                int templateId = result.getInt("template_id");
                TemplateBuilder template = templates.get(templateId);
                if (template == null) throw new SQLException("level template không tồn tại");
                int expectedOrder = nextOrder.getOrDefault(templateId, 0);
                requireSequential(result.getInt("sort_order"), expectedOrder, "level sort_order");
                nextOrder.put(templateId, expectedOrder + 1);
                LevelBuilder level = new LevelBuilder(id,
                        rawByte(result.getInt("point"), "point"),
                        checkedSignedByte(result.getInt("required_level"), "required_level"),
                        checkedShort(result.getInt("mana_use"), "mana_use"), result.getInt("cooldown"),
                        checkedShort(result.getInt("dx"), "dx"), checkedShort(result.getInt("dy"), "dy"),
                        checkedSignedByte(result.getInt("max_fight"), "max_fight"));
                levels.put(id, level);
                template.levels.add(level);
            }
        }
        return levels;
    }

    private static void readLevelOptions(
            Connection connection, Map<Integer, LevelBuilder> levels, int optionCount) throws SQLException {
        Map<Integer, Integer> nextOrder = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(READ_LEVEL_OPTIONS);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                int levelId = result.getInt("skill_level_id");
                LevelBuilder level = levels.get(levelId);
                if (level == null) throw new SQLException("level option tham chiếu level không tồn tại");
                int expectedOrder = nextOrder.getOrDefault(levelId, 0);
                requireSequential(result.getInt("sort_order"), expectedOrder, "level option sort_order");
                nextOrder.put(levelId, expectedOrder + 1);
                int optionId = result.getInt("option_template_id");
                requireIndex(optionId, optionCount, "option_template_id");
                level.options.add(new SkillLevelOptionAsset(
                        checkedShort(result.getInt("parameter_value"), "parameter_value"), (byte) optionId));
            }
        }
    }

    private static SkillAssetBundle build(
            byte version,
            List<String> options,
            List<String> classNames,
            Map<Integer, TemplateBuilder> templates) {
        List<List<SkillTemplateAsset>> byClass = new ArrayList<>();
        for (int index = 0; index < classNames.size(); index++) byClass.add(new ArrayList<>());
        for (TemplateBuilder template : templates.values()) byClass.get(template.classId).add(template.build());
        List<SkillClassAsset> classes = new ArrayList<>();
        for (int id = 0; id < classNames.size(); id++) classes.add(new SkillClassAsset(classNames.get(id), byClass.get(id)));
        return new SkillAssetBundle(version, options, classes);
    }

    private static void requireSequential(int actual, int expected, String field) throws SQLException {
        if (actual != expected) throw new SQLException(field + " phải liên tục từ 0");
    }

    private static void requireIndex(int value, int count, String field) throws SQLException {
        if (value < 0 || value >= count) throw new SQLException(field + " ngoài phạm vi");
    }

    private static byte checkedSignedByte(int value, String field) throws SQLException {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) throw new SQLException(field + " vượt signed byte");
        return (byte) value;
    }

    private static byte rawByte(int value, String field) throws SQLException {
        if (value < 0 || value > 255) throw new SQLException(field + " vượt raw byte");
        return (byte) value;
    }

    private static short checkedShort(int value, String field) throws SQLException {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) throw new SQLException(field + " vượt short");
        return (short) value;
    }

    private static void rollback(Connection connection, Exception original) {
        try { connection.rollback(); } catch (SQLException failure) { original.addSuppressed(failure); }
    }

    private static final class TemplateBuilder {
        final int id; final int classId; final String name; final byte maxPoint; final byte type;
        final short icon; final String description; final List<LevelBuilder> levels = new ArrayList<>();
        TemplateBuilder(int id, int classId, String name, byte maxPoint, byte type, short icon, String description) {
            this.id = id; this.classId = classId; this.name = name; this.maxPoint = maxPoint;
            this.type = type; this.icon = icon; this.description = description;
        }
        SkillTemplateAsset build() {
            return new SkillTemplateAsset((byte) id, name, maxPoint, type, icon, description,
                    levels.stream().map(LevelBuilder::build).toList());
        }
    }

    private static final class LevelBuilder {
        final int id; final byte point; final byte requiredLevel; final short manaUse; final int cooldown;
        final short dx; final short dy; final byte maxFight; final List<SkillLevelOptionAsset> options = new ArrayList<>();
        LevelBuilder(int id, byte point, byte requiredLevel, short manaUse, int cooldown,
                short dx, short dy, byte maxFight) {
            this.id = id; this.point = point; this.requiredLevel = requiredLevel; this.manaUse = manaUse;
            this.cooldown = cooldown; this.dx = dx; this.dy = dy; this.maxFight = maxFight;
        }
        SkillLevelAsset build() {
            return new SkillLevelAsset((short) id, point, requiredLevel, manaUse, cooldown, dx, dy, maxFight, options);
        }
    }
}
