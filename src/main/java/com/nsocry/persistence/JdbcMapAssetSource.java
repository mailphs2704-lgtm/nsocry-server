package com.nsocry.persistence;

import com.nsocry.assets.*;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

/** Adapter JDBC tái dựng MAP catalog trong một repeatable-read snapshot. */
public final class JdbcMapAssetSource implements MapAssetSource {
    private static final String MAPS="SELECT id,name FROM client_map_names ORDER BY id";
    private static final String NPCS="SELECT id,name,head,body,leg,menu_row_count FROM client_npc_templates ORDER BY id";
    private static final String MENUS="SELECT npc_id,row_order,choice_order,text FROM client_npc_menu_entries ORDER BY npc_id,row_order,choice_order";
    private static final String MOBS="SELECT id,type,name,health,move_range,speed FROM client_mob_templates ORDER BY id";
    private final DataSource dataSource; private final byte version;

    /** Tạo source JDBC với version wire explicit; chưa đọc database. */
    public JdbcMapAssetSource(DataSource dataSource,byte version){this.dataSource=Objects.requireNonNull(dataSource);this.version=version;}

    /** Đọc bốn bảng nhất quán, kiểm tra ID/order/range rồi commit snapshot chỉ đọc. */
    @Override public MapAssetBundle load() throws ClientAssetSourceException {
        try(Connection c=dataSource.getConnection()){
            c.setReadOnly(true);c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);c.setAutoCommit(false);
            try{List<String> maps=names(c);List<NpcBuilder> builders=npcs(c);menus(c,builders);List<MobTemplateAsset> mobs=mobs(c);MapAssetBundle b=new MapAssetBundle(version,maps,builders.stream().map(NpcBuilder::build).toList(),mobs);c.commit();return b;}
            catch(SQLException|RuntimeException e){rollback(c,e);throw e;}
        }catch(SQLException|RuntimeException e){throw new ClientAssetSourceException("Không thể đọc MAP asset",e);}
    }
    private static List<String> names(Connection c)throws SQLException{List<String> list=new ArrayList<>();try(PreparedStatement s=c.prepareStatement(MAPS);ResultSet r=s.executeQuery()){while(r.next()){sequential(r.getInt("id"),list.size(),"client_map_names");list.add(Objects.requireNonNull(r.getString("name")));}}return list;}
    private static List<NpcBuilder> npcs(Connection c)throws SQLException{List<NpcBuilder> list=new ArrayList<>();try(PreparedStatement s=c.prepareStatement(NPCS);ResultSet r=s.executeQuery()){while(r.next()){sequential(r.getInt("id"),list.size(),"client_npc_templates");int rows=r.getInt("menu_row_count");if(rows<0||rows>126)throw new SQLException("menu_row_count ngoài phạm vi");list.add(new NpcBuilder(Objects.requireNonNull(r.getString("name")),checkedShort(r.getInt("head"),"head"),checkedShort(r.getInt("body"),"body"),checkedShort(r.getInt("leg"),"leg"),rows));}}return list;}
    private static void menus(Connection c,List<NpcBuilder> npcs)throws SQLException{int previousNpc=-1,previousRow=-1,nextChoice=0;try(PreparedStatement s=c.prepareStatement(MENUS);ResultSet r=s.executeQuery()){while(r.next()){int npc=r.getInt("npc_id"),row=r.getInt("row_order");if(npc<0||npc>=npcs.size())throw new SQLException("menu npc_id ngoài phạm vi");if(row<0||row>=npcs.get(npc).menu.size())throw new SQLException("menu row_order ngoài phạm vi");if(npc!=previousNpc||row!=previousRow){previousNpc=npc;previousRow=row;nextChoice=0;}sequential(r.getInt("choice_order"),nextChoice++,"menu choice_order");npcs.get(npc).menu.get(row).add(Objects.requireNonNull(r.getString("text")));}}}
    private static List<MobTemplateAsset> mobs(Connection c)throws SQLException{List<MobTemplateAsset> list=new ArrayList<>();try(PreparedStatement s=c.prepareStatement(MOBS);ResultSet r=s.executeQuery()){while(r.next()){sequential(r.getInt("id"),list.size(),"client_mob_templates");list.add(new MobTemplateAsset(signedByte(r.getInt("type"),"type"),Objects.requireNonNull(r.getString("name")),r.getInt("health"),signedByte(r.getInt("move_range"),"move_range"),signedByte(r.getInt("speed"),"speed")));}}return list;}
    private static void sequential(int actual,int expected,String field)throws SQLException{if(actual!=expected)throw new SQLException(field+" phải liên tục từ 0");}
    private static byte signedByte(int value,String field)throws SQLException{if(value<Byte.MIN_VALUE||value>Byte.MAX_VALUE)throw new SQLException(field+" vượt signed byte");return(byte)value;}
    private static short checkedShort(int value,String field)throws SQLException{if(value<Short.MIN_VALUE||value>Short.MAX_VALUE)throw new SQLException(field+" vượt short");return(short)value;}
    private static void rollback(Connection c,Exception e){try{c.rollback();}catch(SQLException failure){e.addSuppressed(failure);}}
    private static final class NpcBuilder{final String name;final short head,body,leg;final List<List<String>> menu;NpcBuilder(String name,short head,short body,short leg,int rows){this.name=name;this.head=head;this.body=body;this.leg=leg;menu=new ArrayList<>(rows);for(int i=0;i<rows;i++)menu.add(new ArrayList<>());}NpcTemplateAsset build(){return new NpcTemplateAsset(name,head,body,leg,menu);}}
}
