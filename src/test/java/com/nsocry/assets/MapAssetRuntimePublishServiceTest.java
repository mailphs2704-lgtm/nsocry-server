package com.nsocry.assets;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class MapAssetRuntimePublishServiceTest {
    @Test void publishesOnlyAfterManifestGatesPass()throws Exception{MapAssetBundle b=bundle();var a=MapAssetSeedArtifactGenerator.generate(b);var store=new AtomicMapAssetRuntimeSnapshotStore();var s=new MapAssetRuntimePublishService(()->b,manifest(a),store).rebuildAndPublish();assertSame(s,store.currentSnapshot().orElseThrow());assertArrayEquals(a.payload(),s.payload());assertEquals(7,Byte.toUnsignedInt(s.version()));}
    @Test void checksumFailureKeepsPreviousSnapshot()throws Exception{MapAssetBundle b=bundle();var a=MapAssetSeedArtifactGenerator.generate(b);var store=new AtomicMapAssetRuntimeSnapshotStore();var previous=new MapAssetRuntimePublishService(()->b,manifest(a),store).rebuildAndPublish();var invalid=new MapAssetSeedManifest((byte)7,1,1,1,a.payload().length,"0".repeat(64));assertThrows(IllegalArgumentException.class,()->new MapAssetRuntimePublishService(()->b,invalid,store).rebuildAndPublish());assertSame(previous,store.currentSnapshot().orElseThrow());}
    @Test void sourceFailureDoesNotPublish(){var store=new AtomicMapAssetRuntimeSnapshotStore();var failure=new ClientAssetSourceException("db unavailable",null);var service=new MapAssetRuntimePublishService(()->{throw failure;},new MapAssetSeedManifest((byte)7,0,0,0,1,"0".repeat(64)),store);assertSame(failure,assertThrows(ClientAssetSourceException.class,service::rebuildAndPublish));assertTrue(store.currentSnapshot().isEmpty());}
    @Test void snapshotPayloadIsDefensivelyCopied()throws Exception{var b=bundle();var a=MapAssetSeedArtifactGenerator.generate(b);var s=new MapAssetRuntimePublishService(()->b,manifest(a),new AtomicMapAssetRuntimeSnapshotStore()).rebuildAndPublish();byte[] first=s.payload();first[0]=0;assertEquals(7,Byte.toUnsignedInt(s.payload()[0]));}
    @Test void constructorRejectsMissingDependencies(){var b=bundle();var a=MapAssetSeedArtifactGenerator.generate(b);var m=manifest(a);var store=new AtomicMapAssetRuntimeSnapshotStore();assertThrows(NullPointerException.class,()->new MapAssetRuntimePublishService(null,m,store));assertThrows(NullPointerException.class,()->new MapAssetRuntimePublishService(()->b,null,store));assertThrows(NullPointerException.class,()->new MapAssetRuntimePublishService(()->b,m,null));}
    @Test void snapshotRejectsSameLengthTamperedPayload(){var b=bundle();var a=MapAssetSeedArtifactGenerator.generate(b);byte[] changed=a.payload();changed[changed.length-1]^=1;assertThrows(IllegalArgumentException.class,()->MapAssetRuntimeSnapshot.verified(a.validation(),changed));}
    private static MapAssetSeedManifest manifest(MapAssetSeedArtifact a){var v=a.validation();return new MapAssetSeedManifest(v.version(),v.mapCount(),v.npcCount(),v.mobCount(),v.payloadLength(),v.payloadSha256());}
    private static MapAssetBundle bundle(){return new MapAssetBundle((byte)7,List.of("Làng Cry"),List.of(new NpcTemplateAsset("NPC Cry",(short)1,(short)2,(short)3,List.of(List.of("Chào")))),List.of(new MobTemplateAsset((byte)-1,"Mob Cry",100,(byte)4,(byte)5)));}
}
