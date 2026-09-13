-- V005 DRAFT: archive DATA authoritative; chỉ chạy sau backup/checksum và xác nhận chủ dự án.

CREATE TABLE client_data_assets (
    version TINYINT UNSIGNED NOT NULL,
    task_group_count TINYINT UNSIGNED NOT NULL,
    experience_count TINYINT UNSIGNED NOT NULL,
    payload_length INT UNSIGNED NOT NULL,
    payload_sha256 CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    payload LONGBLOB NOT NULL,
    manifest_text TEXT NOT NULL,
    PRIMARY KEY (version),
    CONSTRAINT chk_client_data_task_groups CHECK (task_group_count <= 127),
    CONSTRAINT chk_client_data_experience CHECK (experience_count <= 255),
    CONSTRAINT chk_client_data_payload_length CHECK (payload_length = OCTET_LENGTH(payload)),
    CONSTRAINT chk_client_data_sha256 CHECK (payload_sha256 REGEXP '^[0-9a-f]{64}$')
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
