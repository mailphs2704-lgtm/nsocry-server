-- V002: read model ITEM tĩnh dành riêng cho client NSOCry.
-- Không chứa inventory người chơi, giá bán, số lượng hoặc thuộc tính ngẫu nhiên.

CREATE TABLE client_item_options (
    id SMALLINT UNSIGNED NOT NULL,
    name VARCHAR(500) NOT NULL,
    type TINYINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_client_item_options_id CHECK (id <= 254)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE TABLE client_item_templates (
    id SMALLINT UNSIGNED NOT NULL,
    type TINYINT NOT NULL,
    gender TINYINT NOT NULL,
    name VARCHAR(500) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    required_level TINYINT NOT NULL,
    icon_id SMALLINT NOT NULL,
    part_id SMALLINT NOT NULL,
    upgradable BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT chk_client_item_templates_id CHECK (id <= 32766)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
