package com.nsocry.bootstrap;

import com.nsocry.assets.MapAssetSeedValidationResult;
import com.nsocry.configuration.*;
import com.nsocry.operations.*;
import com.nsocry.persistence.*;
import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;
import javax.sql.DataSource;

/** Command tương tác import MAP đã duyệt; không migration hoặc publish runtime. */
public final class MapAssetSeedImportCommand {
    private MapAssetSeedImportCommand(){}
    /** Archive + V004 READY + full SHA-256 là ba gate trước transaction. */
    public static void main(String[] args)throws Exception{
        if(args==null||args.length!=1)throw new IllegalArgumentException("map-seed-import yêu cầu đúng một archive path");
        Console console=System.console();if(console==null)throw new IllegalStateException("interactive console is required");
        ValidatedMapAssetSeedArchive archive=new MapAssetSeedArchiveService().readValidated(Path.of(args[0]));
        DatabaseConfiguration config=new DatabaseConfigurationLoader().load(Path.of("config","nsocry.properties"),System.getenv());
        DataSource dataSource=MariaDbDataSourceFactory.create(config);
        if(!new JdbcMapAssetSchemaInspector(dataSource).inspect().ready())throw new IllegalStateException("MAP schema preflight NOT_READY");
        printCandidate(console,archive.validation());String confirmation=console.readLine("Nhập toàn bộ SHA-256 để xác nhận import: ");
        if(!matchesChecksum(archive.validation().payloadSha256(),confirmation))throw new IllegalArgumentException("SHA-256 confirmation không khớp");
        MapAssetSeedValidationResult result=new JdbcMapAssetSeedImporter(dataSource).importSeed(archive.payload(),archive.manifestText());
        console.printf("MAP seed IMPORTED: mapCount=%d, npcCount=%d, mobCount=%d, sha256=%s%n",result.mapCount(),result.npcCount(),result.mobCount(),result.payloadSha256());console.printf("runtimeSnapshotPublished=false%n");
    }
    /** So checksum constant-time, bỏ whitespace ngoài và không phân biệt hoa thường. */
    static boolean matchesChecksum(String expected,String confirmation){Objects.requireNonNull(expected);if(confirmation==null)return false;return MessageDigest.isEqual(expected.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII),confirmation.trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII));}
    private static void printCandidate(Console c,MapAssetSeedValidationResult v){c.printf("MAP seed candidate: version=%d, maps=%d, npcs=%d, mobs=%d%n",Byte.toUnsignedInt(v.version()),v.mapCount(),v.npcCount(),v.mobCount());c.printf("payloadLength=%d, sha256=%s%n",v.payloadLength(),v.payloadSha256());c.printf("Schema V004 READY và backup phải được xác nhận trước thao tác này.%n");}
}
