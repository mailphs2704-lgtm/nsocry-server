# Kiểm kê source NSOCry tại Architecture Lock v1

File này chụp toàn bộ type production hiện có tại lúc khóa khung. Public method cụ thể được giải thích bằng Javadoc và tài liệu module hiện hành; contract tương lai nằm trong `planned-contracts.tsv`.

## com.nsocry.assets

`AppearanceAssetBundle`, `AppearanceAssetCodec`, `AppearanceAssetSource`, `AppearanceLayerAsset`, `AppearancePartAsset`, `AtomicClientAssetSnapshotProvider`, `ClientAssetSnapshot`, `ClientAssetSnapshotAssembler`, `ClientAssetSnapshotBuildService`, `ClientAssetSnapshotProvider`, `ClientAssetSnapshotPublisher`, `ClientAssetSourceException`, `ClientGraphicBlock`, `DataAssetBundle`, `DataAssetCodec`, `DataAssetSource`, `ItemAssetBundle`, `ItemAssetCodec`, `ItemAssetSeedArtifact`, `ItemAssetSeedArtifactGenerator`, `ItemAssetSeedManifest`, `ItemAssetSeedManifestParser`, `ItemAssetSeedValidationException`, `ItemAssetSeedValidator`, `ItemAssetSource`, `ItemAssetValidationResult`, `ItemOptionAsset`, `ItemTemplateAsset`, `LegAppearanceAsset`, `MapAssetBundle`, `MapAssetCodec`, `MapAssetSource`, `MobTemplateAsset`, `MountAppearanceAsset`, `NpcTemplateAsset`, `ProgressionTable`, `SkillAssetBundle`, `SkillAssetCodec`, `SkillAssetSeedArtifact`, `SkillAssetSeedArtifactGenerator`, `SkillAssetSeedManifest`, `SkillAssetSeedManifestParser`, `SkillAssetSeedValidationResult`, `SkillAssetSeedValidator`, `SkillAssetSource`, `SkillAssetStructureValidator`, `SkillAssetValidationReport`, `SkillClassAsset`, `SkillLevelAsset`, `SkillLevelOptionAsset`, `SkillTemplateAsset`, `TaskRouteAsset`, `package-info`.

## com.nsocry.assets.conversion

`ItemAssetConversionReport`, `ItemAssetConversionResult`, `ReferenceItemAssetConverter`, `ReferenceItemDumpRows`, `ReferenceItemOptionRow`, `ReferenceItemSqlDumpParser`, `ReferenceItemTemplateRow`, `ReferenceSkillAssetConverter`, `ReferenceSkillDumpInventoryParser`, `SkillAssetConversionResult`, `SkillDumpInventoryReport`, `SkillRawByteDifference`.

## com.nsocry.authentication

`AccountCredential`, `AccountProvisioningRepository`, `AccountRepository`, `AccountRole`, `AccountStatus`, `AuthenticationService`, `FirstAdministratorService`, `PasswordHashingPort`, `Pbkdf2PasswordHasher`, `package-info`.

## com.nsocry.bootstrap

`FirstAdministratorCommand`, `ItemAssetDatabaseVerifyCommand`, `ItemAssetSchemaPreflightCommand`, `ItemAssetSeedConvertCommand`, `ItemAssetSeedDryRunCommand`, `ItemAssetSeedImportCommand`, `NsocryLauncher`, `NsocryServerApplication`, `SkillAssetDatabaseVerifyCommand`, `SkillAssetSchemaPreflightCommand`, `SkillAssetSeedConvertCommand`, `SkillAssetSeedDryRunCommand`, `SkillAssetSeedImportCommand`, `package-info`.

## com.nsocry.character

`CharacterSelectionPayloadCodec`, `CharacterSummary`, `CreateCharacterRequest`, `package-info`.

## com.nsocry.configuration

`DatabaseConfiguration`, `DatabaseConfigurationLoader`, `ServerConfiguration`, `ServerConfigurationLoader`, `package-info`.

## com.nsocry.network

`LegacyHandshakeConnectionHandler`, `NetworkEventSink`, `SessionConnectionHandler`, `TcpServer`, `TcpServerConfig`, `package-info`.

## com.nsocry.observability

`SanitizedNetworkEventSink`, `package-info`.

## com.nsocry.operations

`ItemAssetSeedArchiveService`, `SkillAssetSeedArchiveService`, `ValidatedItemAssetSeedArchive`, `ValidatedSkillAssetSeedArchive`.

## com.nsocry.persistence

`AccountPersistenceException`, `ItemAssetSchemaColumn`, `ItemAssetSchemaContract`, `ItemAssetSchemaPreflightReport`, `ItemAssetSeedImportException`, `JdbcAccountProvisioningRepository`, `JdbcAccountRepository`, `JdbcItemAssetSchemaInspector`, `JdbcItemAssetSeedImporter`, `JdbcItemAssetSource`, `JdbcSkillAssetSchemaInspector`, `JdbcSkillAssetSeedImporter`, `JdbcSkillAssetSource`, `MariaDbDataSourceFactory`, `SkillAssetSchemaColumn`, `SkillAssetSchemaContract`, `SkillAssetSchemaPreflightReport`, `SkillAssetSeedImportException`, `package-info`.

## com.nsocry.protocol.compat

`ClientDataSet`, `ClientVersionManifest`, `LegacyFrameCodec`, `LegacyFrameReader`, `LegacyFrameWriter`, `LegacyKeyCodec`, `PostLoginVersionPayloadCodec`, `ProtocolFrame`, `ProtocolLimits`, `RollingXorCipher`, `package-info`.

## com.nsocry.session

`AuthenticationDecision`, `AuthenticationPort`, `ClientInfo`, `HandshakeEvent`, `HandshakePayloadDecoder`, `HandshakeProcessor`, `HandshakeStateMachine`, `LegacySessionTransport`, `LoginRequest`, `ProtocolStateException`, `SecureRandomSessionKeyProvider`, `SessionKeyProvider`, `SessionPhase`, `package-info`.

Tổng: **154 Java type** trong **12 package** hiện hữu.
