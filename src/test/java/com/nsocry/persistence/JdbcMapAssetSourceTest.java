package com.nsocry.persistence;

import static org.junit.jupiter.api.Assertions.*;
import com.nsocry.assets.ClientAssetSourceException;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class JdbcMapAssetSourceTest {
    @Test void rebuildsEmptyMenuRowInRepeatableReadSnapshot()throws Exception{FakeJdbc j=FakeJdbc.valid();var b=new JdbcMapAssetSource(j.source(),(byte)7).load();assertEquals("Làng Cry",b.mapNames().get(0));assertTrue(b.npcs().get(0).menu().get(0).isEmpty());assertEquals("Nói chuyện",b.npcs().get(0).menu().get(1).get(0));assertEquals(-1,b.mobs().get(0).type());assertTrue(j.readOnly);assertTrue(j.committed);assertEquals(Connection.TRANSACTION_REPEATABLE_READ,j.isolation);}
    @Test void idGapRollsBackSnapshot(){FakeJdbc j=FakeJdbc.valid();j.maps.get(0).put("id",1);assertThrows(ClientAssetSourceException.class,()->new JdbcMapAssetSource(j.source(),(byte)7).load());assertTrue(j.rolledBack);assertFalse(j.committed);}
    private static Map<String,Object> row(Object...v){Map<String,Object> r=new HashMap<>();for(int i=0;i<v.length;i+=2)r.put((String)v[i],v[i+1]);return r;}
    private static final class FakeJdbc{
        final List<Map<String,Object>> maps=new ArrayList<>(),npcs=new ArrayList<>(),menus=new ArrayList<>(),mobs=new ArrayList<>();boolean readOnly,committed,rolledBack;int isolation;
        static FakeJdbc valid(){FakeJdbc j=new FakeJdbc();j.maps.add(row("id",0,"name","Làng Cry"));j.npcs.add(row("id",0,"name","NPC Cry","head",1,"body",2,"leg",3,"menu_row_count",2));j.menus.add(row("npc_id",0,"row_order",1,"choice_order",0,"text","Nói chuyện"));j.mobs.add(row("id",0,"type",-1,"name","Mob Cry","health",100,"move_range",4,"speed",5));return j;}
        DataSource source(){return(DataSource)Proxy.newProxyInstance(getClass().getClassLoader(),new Class<?>[]{DataSource.class},(p,m,a)->m.getName().equals("getConnection")?connection():value(m.getReturnType()));}
        Connection connection(){return(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class<?>[]{Connection.class},(p,m,a)->switch(m.getName()){case"setReadOnly"->{readOnly=(Boolean)a[0];yield null;}case"setTransactionIsolation"->{isolation=(Integer)a[0];yield null;}case"prepareStatement"->statement(rows((String)a[0]));case"commit"->{committed=true;yield null;}case"rollback"->{rolledBack=true;yield null;}default->value(m.getReturnType());});}
        List<Map<String,Object>> rows(String sql){if(sql.contains("client_npc_menu_entries"))return menus;if(sql.contains("client_npc_templates"))return npcs;if(sql.contains("client_mob_templates"))return mobs;return maps;}
        PreparedStatement statement(List<Map<String,Object>> rows){return(PreparedStatement)Proxy.newProxyInstance(getClass().getClassLoader(),new Class<?>[]{PreparedStatement.class},(p,m,a)->m.getName().equals("executeQuery")?result(rows):value(m.getReturnType()));}
        ResultSet result(List<Map<String,Object>> rows){int[] i={-1};return(ResultSet)Proxy.newProxyInstance(getClass().getClassLoader(),new Class<?>[]{ResultSet.class},(p,m,a)->{if(m.getName().equals("next"))return++i[0]<rows.size();if(m.getName().startsWith("get"))return rows.get(i[0]).get((String)a[0]);return value(m.getReturnType());});}
    }
    private static Object value(Class<?> t){if(!t.isPrimitive())return null;if(t==boolean.class)return false;if(t==int.class)return 0;if(t==long.class)return 0L;return 0;}
}
