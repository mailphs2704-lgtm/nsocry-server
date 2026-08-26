package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.*;
import com.nsocry.assets.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class MapAssetPersistenceCommandsTest {
    private static final String SHA="0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
    @Test void acceptsChecksumIgnoringCaseAndWhitespace(){assertTrue(MapAssetSeedImportCommand.matchesChecksum(SHA,"  "+SHA.toUpperCase()+"  "));}
    @Test void rejectsChangedOrCancelledChecksum(){assertFalse(MapAssetSeedImportCommand.matchesChecksum(SHA,"1"+SHA.substring(1)));assertFalse(MapAssetSeedImportCommand.matchesChecksum(SHA,null));}
    @Test void verifiesSourceAgainstManifest()throws Exception{var a=MapAssetSeedArtifactGenerator.generate(fixture());var r=MapAssetDatabaseVerifyCommand.verify(MapAssetPersistenceCommandsTest::fixture,a.manifestText());assertEquals(a.validation().payloadSha256(),r.payloadSha256());}
    @Test void reportDeclaresNoVerifierMutation(){var r=MapAssetSeedArtifactGenerator.generate(fixture()).validation();var b=new ByteArrayOutputStream();MapAssetDatabaseVerifyCommand.printReport(r,new PrintStream(b,true,StandardCharsets.UTF_8));String text=b.toString(StandardCharsets.UTF_8);assertTrue(text.contains("MAP database payload VERIFIED"));assertTrue(text.contains("databaseChanged=false"));assertTrue(text.contains("runtimeSnapshotPublished=false"));}
    @Test void launcherRoutesImportAndDatabaseVerify(){assertEquals(NsocryLauncher.LaunchCommand.MAP_SEED_IMPORT,NsocryLauncher.parse(new String[]{"map-seed-import","map.zip"}).command());assertEquals(NsocryLauncher.LaunchCommand.MAP_SEED_DB_VERIFY,NsocryLauncher.parse(new String[]{"map-seed-db-verify","map.zip"}).command());}
    private static MapAssetBundle fixture(){return new MapAssetBundle((byte)7,List.of("Làng Cry"),List.of(new NpcTemplateAsset("NPC Cry",(short)1,(short)2,(short)3,List.of(List.of(),List.of("Nói chuyện")))),List.of(new MobTemplateAsset((byte)-1,"Mob Cry",100,(byte)4,(byte)5)));}
}
