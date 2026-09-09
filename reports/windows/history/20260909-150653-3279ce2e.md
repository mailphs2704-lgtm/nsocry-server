# BÃ¡o cÃ¡o build/test Windows NSOCry

- Tráº¡ng thÃ¡i: **SUCCESS**
- NhÃ¡nh: agent/document-nsokiss-runtime
- Commit Ä‘Æ°á»£c kiá»ƒm tra: 3279ce2e80a9b85317dbca5c9120f4cea53d82c8
- Báº¯t Ä‘áº§u UTC: 2026-09-09T15:06:53.1895717Z
- Káº¿t thÃºc UTC: 2026-09-09T15:07:02.2464743Z
- Maven exit code: 0
- Tá»•ng há»£p test: Tests run: 321, Failures: 0, Errors: 0, Skipped: 0
- Java: java version "19.0.2" 2023-01-17
- Maven: Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
- Lá»‡nh: mvn clean package
- Database changed: false
- DATA imported: false
- Runtime snapshot published: false
- Server startup wired: false

## Ã nghÄ©a

BÃ¡o cÃ¡o nÃ y do NSOCRY_WORK.bat táº¡o trÃªn mÃ¡y Windows cá»§a chá»§ dá»± Ã¡n. BÃ¡o cÃ¡o xÃ¡c nháº­n kháº£ nÄƒng compile/package vÃ  káº¿t quáº£ test cá»§a Ä‘Ãºng commit nÃªu trÃªn. Quy trÃ¬nh khÃ´ng cháº¡y migration, khÃ´ng import DATA vÃ  khÃ´ng khá»Ÿi Ä‘á»™ng server.

## Nháº­t kÃ½ Ä‘áº§y Ä‘á»§

Nháº­t kÃ½ Maven Ä‘áº§y Ä‘á»§ Ä‘Æ°á»£c giá»¯ cá»¥c bá»™ táº¡i .nsocry-work/maven-latest.log Ä‘á»ƒ trÃ¡nh lÃ m repository phÃ¬nh lá»›n. Khi build lá»—i, pháº§n cuá»‘i log Ä‘Æ°á»£c chÃ©p dÆ°á»›i Ä‘Ã¢y.

## Pháº§n cuá»‘i Maven log

```
text
[INFO] Running com.nsocry.bootstrap.SkillAssetSeedImportCommandTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in com.nsocry.bootstrap.SkillAssetSeedImportCommandTest
[INFO] Running com.nsocry.character.CharacterSelectionPayloadCodecTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in com.nsocry.character.CharacterSelectionPayloadCodecTest
[INFO] Running com.nsocry.configuration.DatabaseConfigurationTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in com.nsocry.configuration.DatabaseConfigurationTest
[INFO] Running com.nsocry.configuration.ServerConfigurationTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in com.nsocry.configuration.ServerConfigurationTest
[INFO] Running com.nsocry.network.LegacyHandshakeLoopbackTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.010 s -- in com.nsocry.network.LegacyHandshakeLoopbackTest
[INFO] Running com.nsocry.network.TcpServerTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in com.nsocry.network.TcpServerTest
[INFO] Running com.nsocry.observability.SanitizedNetworkEventSinkTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in com.nsocry.observability.SanitizedNetworkEventSinkTest
[INFO] Running com.nsocry.operations.DataAssetSeedArchiveServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.019 s -- in com.nsocry.operations.DataAssetSeedArchiveServiceTest
[INFO] Running com.nsocry.operations.ItemAssetSeedArchiveServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.022 s -- in com.nsocry.operations.ItemAssetSeedArchiveServiceTest
[INFO] Running com.nsocry.persistence.DataAssetSchemaPreflightTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.023 s -- in com.nsocry.persistence.DataAssetSchemaPreflightTest
[INFO] Running com.nsocry.persistence.ItemAssetSchemaPreflightTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in com.nsocry.persistence.ItemAssetSchemaPreflightTest
[INFO] Running com.nsocry.persistence.JdbcAccountRepositoryTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.004 s -- in com.nsocry.persistence.JdbcAccountRepositoryTest
[INFO] Running com.nsocry.persistence.JdbcItemAssetSeedImporterTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in com.nsocry.persistence.JdbcItemAssetSeedImporterTest
[INFO] Running com.nsocry.persistence.JdbcItemAssetSourceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in com.nsocry.persistence.JdbcItemAssetSourceTest
[INFO] Running com.nsocry.persistence.JdbcMapAssetSeedImporterTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.006 s -- in com.nsocry.persistence.JdbcMapAssetSeedImporterTest
[INFO] Running com.nsocry.persistence.JdbcMapAssetSourceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.004 s -- in com.nsocry.persistence.JdbcMapAssetSourceTest
[INFO] Running com.nsocry.persistence.JdbcSkillAssetSeedImporterTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.006 s -- in com.nsocry.persistence.JdbcSkillAssetSeedImporterTest
[INFO] Running com.nsocry.persistence.JdbcSkillAssetSourceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in com.nsocry.persistence.JdbcSkillAssetSourceTest
[INFO] Running com.nsocry.persistence.MapAssetSchemaPreflightTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.006 s -- in com.nsocry.persistence.MapAssetSchemaPreflightTest
[INFO] Running com.nsocry.persistence.SkillAssetSchemaPreflightTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.006 s -- in com.nsocry.persistence.SkillAssetSchemaPreflightTest
[INFO] Running com.nsocry.protocol.compat.LegacyFrameStreamTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in com.nsocry.protocol.compat.LegacyFrameStreamTest
[INFO] Running com.nsocry.protocol.compat.PostLoginVersionPayloadCodecTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in com.nsocry.protocol.compat.PostLoginVersionPayloadCodecTest
[INFO] Running com.nsocry.protocol.compat.ProtocolFixtureTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.007 s -- in com.nsocry.protocol.compat.ProtocolFixtureTest
[INFO] Running com.nsocry.session.HandshakePayloadDecoderTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in com.nsocry.session.HandshakePayloadDecoderTest
[INFO] Running com.nsocry.session.HandshakeProcessorTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in com.nsocry.session.HandshakeProcessorTest
[INFO] Running com.nsocry.session.HandshakeStateMachineTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in com.nsocry.session.HandshakeStateMachineTest
[INFO] Running com.nsocry.session.LegacySessionTransportTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 s -- in com.nsocry.session.LegacySessionTransportTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 321, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] 
[INFO] --- jar:3.5.0:jar (default-jar) @ nsocry-server ---
[INFO] Building jar: C:\Users\15130\Desktop\Project NSOCRY\nsocry-server\target\nsocry-server-0.1.0-SNAPSHOT.jar
[INFO] 
[INFO] --- shade:3.6.2:shade (default) @ nsocry-server ---
[WARNING] mariadb-java-client-3.5.10.jar, nsocry-server-0.1.0-SNAPSHOT.jar define 1 overlapping resource: 
[WARNING]   - META-INF/MANIFEST.MF
[WARNING] maven-shade-plugin has detected that some files are
[WARNING] present in two or more JARs. When this happens, only one
[WARNING] single version of the file is copied to the uber jar.
[WARNING] Usually this is not harmful and you can skip these warnings,
[WARNING] otherwise try to manually exclude artifacts based on
[WARNING] mvn dependency:tree -Ddetail=true and the above output.
[WARNING] See https://maven.apache.org/plugins/maven-shade-plugin/
[INFO] Replacing original artifact with shaded artifact.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.528 s
[INFO] Finished at: 2026-09-09T22:07:02+07:00
[INFO] ------------------------------------------------------------------------
```
