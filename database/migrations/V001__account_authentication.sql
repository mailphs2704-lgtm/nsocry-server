-- V001: dữ liệu tối thiểu cho tài khoản và xác thực NSOCry.
-- Không chứa dữ liệu seed, mật khẩu rõ hoặc dữ liệu nhập từ database reference.

CREATE TABLE accounts (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
    password_hash VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    status SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    activated BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(24) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT 'PLAYER',
    failed_login_count SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    locked_until DATETIME(3) NULL,
    last_login_at DATETIME(3) NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
        ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (id),
    CONSTRAINT uq_accounts_username UNIQUE (username),
    CONSTRAINT chk_accounts_username_length
        CHECK (CHAR_LENGTH(username) BETWEEN 3 AND 32),
    CONSTRAINT chk_accounts_status
        CHECK (status IN (0, 1, 2)),
    CONSTRAINT chk_accounts_role
        CHECK (role IN ('PLAYER', 'MODERATOR', 'ADMINISTRATOR'))
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
