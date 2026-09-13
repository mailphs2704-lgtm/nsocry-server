package com.nsocry.persistence;

import static org.junit.jupiter.api.Assertions.*;
import com.nsocry.assets.*;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class JdbcMapAssetSeedImporterTest {
    @Test void commitsFourTablesAndPreservesEmptyMenuRow()throws Exception{FakeJdbc j=new FakeJdbc();var a=MapAssetSeedArtifactGenerator.generate(fixture());var r=new JdbcMapAssetSeedImporter(j.source()).importSeed(a.payload(),a.manifestText());assertEquals(1,r.mapCount());assertEquals(2,j.rows.get("client_npc_templates").get(0).get(6));assertEquals(1,j.rows.get("client_npc_menu_entries").size());assertTrue(j.committed);assertEquals(Connection.TRANSACTION_SERIALIZABLE,j.isolation);}
    @Test void invalidArtifactNeverOpensConnection(){FakeJdbc j=new FakeJdbc();var a=MapAssetSeedArtifactGenerator.generate(fixture());byte[] p=a.payload();p[p.length-1]^=1;assertThrows(MapAssetSeedImportException.class,()->new JdbcMapAssetSeedImporter(j.source()).importSeed(p,a.manifestText()));assertEquals(0,j.connections);}
    @Test void failedBatchRollsBack(){FakeJdbc j=new FakeJdbc();j.fail=true;var a=MapAssetSeedArtifactGenerator.generate(fixture());assertThrows(MapAssetSeedImportException.class,()->new JdbcMapAssetSeedImporter(j.source()).importSeed(a.payload(),a.manifestText()));assertTrue(j.rolledBack);assertFalse(j.committed);}
    private static MapAssetBundle fixture(){return new MapAssetBundle((byte)7,List.of("Làng Cry"),List.of(new NpcTemplateAsset("NPC Cry",(short)1,(short)2,(short)3,List.of(List.of(),List.of("Nói chuyện")))),List.of(new MobTemplateAsset((byte)-1,"Mob Cry",100,(byte)4,(byte)5)));}
    private static final class FakeJdbc{
        final Map<String,List<Map<Integer,Object>>> rows=new HashMap<>();int connections,isolation;boolean committed,rolledBack,fail;
        DataSource source(){return(DataSource)Proxy.newProxyInstance(getClass().getClassLoader(),new Class<?>[]{DataSource.class},(p,m,a)->m.getName().equals("getConnection")?(connections++>=0?connection():null):value(m.getReturnType()));}
        Connection connection(){return(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class<?>[]{Connection.class},(p,m,a)->switch(m.getName()){case"setTransactionIsolation"->{isolation=(Integer)a[0];yield null;}case"prepareStatement"->statement((String)a[0]);case"commit"->{committed=true;yield null;}case"rollback"->{rolledBack=true;yield null;}default->value(m.getReturnType());});}
        PreparedStatement statement(String sql){String table=table(sql);Map<Integer,Object> current=new HashMap<>();List<Map<Integer,Object>> batch=rows.computeIfAbsent(table,k->new ArrayList<>());return(PreparedStatement)Proxy.newProxyInstance(getClass().getClassLoader(),new Class<?>[]{PreparedStatement.class},(p,m,a)->{if(m.getName().startsWith("set")){current.put((Integer)a[0],a[1]);return null;}if(m.getName().equals("addBatch")){batch.add(new HashMap<>(current));return null;}if(m.getName().equals("executeUpdate"))return 1;if(m.getName().equals("executeBatch")){if(fail)throw new SQLException("fail");int[] result=new int[batch.size()];Arrays.fill(result,Statement.SUCCESS_NO_INFO);return result;}return value(m.getReturnType());});}
        static String table(String sql){String n=sql.strip();String prefix=n.startsWith("DELETE FROM ")?"DELETE FROM ":"INSERT INTO ";return n.substring(prefix.length()).split("[ (]",2)[0];}
    }
    private static Object value(Class<?> t){if(!t.isPrimitive())return null;if(t==boolean.class)return false;if(t==int.class)return 0;if(t==long.class)return 0L;return 0;}
}
