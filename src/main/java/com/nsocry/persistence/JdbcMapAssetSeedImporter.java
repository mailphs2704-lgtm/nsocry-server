package com.nsocry.persistence;

import com.nsocry.assets.*;
import java.sql.*;
import java.util.Objects;
import javax.sql.DataSource;

/** Thay toàn bộ MAP seed đã kiểm định trong một transaction SERIALIZABLE. */
public final class JdbcMapAssetSeedImporter {
    private static final String[] DELETE_SQL = {"DELETE FROM client_npc_menu_entries",
            "DELETE FROM client_npc_templates", "DELETE FROM client_mob_templates",
            "DELETE FROM client_map_names"};
    private static final String INSERT_MAP = "INSERT INTO client_map_names (id, name) VALUES (?, ?)";
    private static final String INSERT_NPC = "INSERT INTO client_npc_templates (id,name,head,body,leg,menu_row_count) VALUES (?,?,?,?,?,?)";
    private static final String INSERT_MENU = "INSERT INTO client_npc_menu_entries (npc_id,row_order,choice_order,text) VALUES (?,?,?,?)";
    private static final String INSERT_MOB = "INSERT INTO client_mob_templates (id,type,name,health,move_range,speed) VALUES (?,?,?,?,?,?)";
    private final DataSource dataSource;

    /** Tạo importer transaction; chưa mở connection. */
    public JdbcMapAssetSeedImporter(DataSource dataSource) { this.dataSource = Objects.requireNonNull(dataSource); }

    /** Validate trước connection; commit đủ bốn bảng hoặc rollback toàn bộ. */
    public MapAssetSeedValidationResult importSeed(byte[] payload, String manifestText)
            throws MapAssetSeedImportException {
        Objects.requireNonNull(payload); Objects.requireNonNull(manifestText);
        final MapAssetBundle bundle; final MapAssetSeedValidationResult validation;
        try {
            MapAssetSeedManifest manifest = MapAssetSeedManifestParser.parse(manifestText);
            bundle = MapAssetCodec.decode(payload.clone());
            validation = MapAssetSeedValidator.validate(bundle, manifest);
            if (payload.length != validation.payloadLength()) throw new IllegalArgumentException("Sai payload length");
        } catch (Exception e) { throw new MapAssetSeedImportException("MAP artifact không hợp lệ", e); }
        try (Connection c = dataSource.getConnection()) {
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); c.setAutoCommit(false);
            try { replace(c, bundle); c.commit(); return validation; }
            catch (SQLException | RuntimeException e) { rollback(c, e); throw e; }
        } catch (SQLException | RuntimeException e) { throw new MapAssetSeedImportException("Không thể ghi MAP seed", e); }
    }

    private static void replace(Connection c, MapAssetBundle b) throws SQLException {
        for (String sql : DELETE_SQL) try (PreparedStatement s = c.prepareStatement(sql)) { s.executeUpdate(); }
        try (PreparedStatement s = c.prepareStatement(INSERT_MAP)) {
            for (int id=0; id<b.mapNames().size(); id++) { s.setInt(1,id); s.setString(2,b.mapNames().get(id)); s.addBatch(); }
            requireBatch(s.executeBatch(), b.mapNames().size(), "map names");
        }
        try (PreparedStatement s = c.prepareStatement(INSERT_NPC)) {
            for (int id=0; id<b.npcs().size(); id++) { NpcTemplateAsset n=b.npcs().get(id); s.setInt(1,id); s.setString(2,n.name()); s.setShort(3,n.head()); s.setShort(4,n.body()); s.setShort(5,n.leg()); s.setInt(6,n.menu().size()); s.addBatch(); }
            requireBatch(s.executeBatch(), b.npcs().size(), "npc templates");
        }
        int menuCount=0;
        try (PreparedStatement s = c.prepareStatement(INSERT_MENU)) {
            for (int npc=0;npc<b.npcs().size();npc++) for(int row=0;row<b.npcs().get(npc).menu().size();row++) for(int choice=0;choice<b.npcs().get(npc).menu().get(row).size();choice++) { s.setInt(1,npc);s.setInt(2,row);s.setInt(3,choice);s.setString(4,b.npcs().get(npc).menu().get(row).get(choice));s.addBatch();menuCount++; }
            requireBatch(s.executeBatch(), menuCount, "npc menu entries");
        }
        try (PreparedStatement s = c.prepareStatement(INSERT_MOB)) {
            for (int id=0;id<b.mobs().size();id++) { MobTemplateAsset m=b.mobs().get(id);s.setInt(1,id);s.setByte(2,m.type());s.setString(3,m.name());s.setInt(4,m.health());s.setByte(5,m.moveRange());s.setByte(6,m.speed());s.addBatch(); }
            requireBatch(s.executeBatch(), b.mobs().size(), "mob templates");
        }
    }
    private static void requireBatch(int[] results,int expected,String name)throws SQLException{if(results.length!=expected)throw new SQLException(name+" batch count không khớp");for(int result:results)if(result==Statement.EXECUTE_FAILED)throw new SQLException(name+" batch thất bại");}
    private static void rollback(Connection c,Exception e){try{c.rollback();}catch(SQLException failure){e.addSuppressed(failure);}}
}
