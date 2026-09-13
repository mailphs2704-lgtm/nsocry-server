package com.nsocry.bootstrap;

import static org.junit.jupiter.api.Assertions.*;
import com.nsocry.assets.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class MapAssetRuntimePublishCommandTest {
    @Test void publishesValidatedJdbcEquivalentBundle()throws Exception{var b=bundle();var a=MapAssetSeedArtifactGenerator.generate(b);var store=new AtomicMapAssetRuntimeSnapshotStore();var s=MapAssetRuntimePublishCommand.publish(()->b,a.manifestText(),store);assertSame(s,store.currentSnapshot().orElseThrow());assertEquals(7,Byte.toUnsignedInt(s.version()));}
    @Test void invalidChecksumDoesNotPublish(){var b=bundle();var a=MapAssetSeedArtifactGenerator.generate(b);String bad=a.manifestText().replace(a.validation().payloadSha256(),"0".repeat(64));var store=new AtomicMapAssetRuntimeSnapshotStore();assertThrows(IllegalArgumentException.class,()->MapAssetRuntimePublishCommand.publish(()->b,bad,store));assertTrue(store.currentSnapshot().isEmpty());}
    @Test void reportStatesReadOnlyAndNotStartupWired()throws Exception{var b=bundle();var a=MapAssetSeedArtifactGenerator.generate(b);var store=new AtomicMapAssetRuntimeSnapshotStore();var s=MapAssetRuntimePublishCommand.publish(()->b,a.manifestText(),store);var bytes=new ByteArrayOutputStream();MapAssetRuntimePublishCommand.printReport(s,store,new PrintStream(bytes));String report=bytes.toString(StandardCharsets.UTF_8);assertTrue(report.contains("runtimeSnapshotPublished=true"));assertTrue(report.contains("databaseChanged=false"));assertTrue(report.contains("serverStartupWired=false"));}
    @Test void reportRejectsSnapshotThatIsNotCurrent()throws Exception{var b=bundle();var a=MapAssetSeedArtifactGenerator.generate(b);var s=MapAssetRuntimePublishCommand.publish(()->b,a.manifestText(),new AtomicMapAssetRuntimeSnapshotStore());assertThrows(IllegalStateException.class,()->MapAssetRuntimePublishCommand.printReport(s,new AtomicMapAssetRuntimeSnapshotStore(),System.out));}
    @Test void launcherRoutesMapRuntimePublish(){assertEquals(NsocryLauncher.LaunchCommand.MAP_RUNTIME_PUBLISH,NsocryLauncher.parse(new String[]{"map-runtime-publish","map.zip"}).command());}
    private static MapAssetBundle bundle(){return new MapAssetBundle((byte)7,List.of("Làng Cry"),List.of(new NpcTemplateAsset("NPC Cry",(short)1,(short)2,(short)3,List.of())),List.of(new MobTemplateAsset((byte)1,"Mob Cry",100,(byte)4,(byte)5)));}
}
