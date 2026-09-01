package com.nsocry.bootstrap;

import com.nsocry.assets.*;
import com.nsocry.configuration.*;
import com.nsocry.operations.*;
import com.nsocry.persistence.*;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;

/** Xác minh dữ liệu MAP trong database tái tạo đúng payload candidate. */
public final class MapAssetDatabaseVerifyCommand {
    private MapAssetDatabaseVerifyCommand(){}
    /** Đọc archive/schema/JDBC source và so payload end-to-end, không ghi database. */
    public static void main(String[] args)throws Exception{if(args==null||args.length!=1)throw new IllegalArgumentException("map-seed-db-verify yêu cầu đúng một archive path");ValidatedMapAssetSeedArchive a=new MapAssetSeedArchiveService().readValidated(Path.of(args[0]));DatabaseConfiguration c=new DatabaseConfigurationLoader().load(Path.of("config","nsocry.properties"),System.getenv());DataSource ds=MariaDbDataSourceFactory.create(c);if(!new JdbcMapAssetSchemaInspector(ds).inspect().ready())throw new IllegalStateException("MAP schema preflight NOT_READY");printReport(verify(new JdbcMapAssetSource(ds,a.validation().version()),a.manifestText()),System.out);}
    /** Load qua source port rồi validate bundle với manifest candidate. */
    static MapAssetSeedValidationResult verify(MapAssetSource source,String manifestText)throws Exception{Objects.requireNonNull(source);MapAssetSeedManifest manifest=MapAssetSeedManifestParser.parse(manifestText);MapAssetBundle bundle=Objects.requireNonNull(source.load());return MapAssetSeedValidator.validate(bundle,manifest);}
    /** In count/checksum và khẳng định verifier không đổi database/runtime. */
    static void printReport(MapAssetSeedValidationResult r,PrintStream out){Objects.requireNonNull(r);Objects.requireNonNull(out);out.println("MAP database payload VERIFIED");out.println("version="+Byte.toUnsignedInt(r.version()));out.println("mapCount="+r.mapCount());out.println("npcCount="+r.npcCount());out.println("mobCount="+r.mobCount());out.println("payloadLength="+r.payloadLength());out.println("sha256="+r.payloadSha256());out.println("databaseChanged=false");out.println("runtimeSnapshotPublished=false");}
}
