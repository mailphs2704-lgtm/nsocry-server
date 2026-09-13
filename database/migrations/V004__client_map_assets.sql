-- V004 DRAFT: read model MAP tĩnh; chỉ chạy sau backup/checksum và xác nhận chủ dự án.

CREATE TABLE client_map_names (
    id TINYINT UNSIGNED NOT NULL,
    name VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_client_map_names_id CHECK (id <= 254)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE client_npc_templates (
    id TINYINT UNSIGNED NOT NULL,
    name VARCHAR(500) NOT NULL,
    head SMALLINT NOT NULL,
    body SMALLINT NOT NULL,
    leg SMALLINT NOT NULL,
    menu_row_count TINYINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_client_npc_templates_id CHECK (id <= 126),
    CONSTRAINT chk_client_npc_menu_rows CHECK (menu_row_count <= 126)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE client_npc_menu_entries (
    npc_id TINYINT UNSIGNED NOT NULL,
    row_order TINYINT UNSIGNED NOT NULL,
    choice_order TINYINT UNSIGNED NOT NULL,
    text VARCHAR(5000) NOT NULL,
    PRIMARY KEY (npc_id, row_order, choice_order),
    CONSTRAINT chk_client_npc_menu_row CHECK (row_order <= 126),
    CONSTRAINT chk_client_npc_menu_choice CHECK (choice_order <= 126),
    CONSTRAINT fk_client_npc_menu_template FOREIGN KEY (npc_id)
        REFERENCES client_npc_templates (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE client_mob_templates (
    id SMALLINT UNSIGNED NOT NULL,
    type TINYINT NOT NULL,
    name VARCHAR(500) NOT NULL,
    health INT NOT NULL,
    move_range TINYINT NOT NULL,
    speed TINYINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_client_mob_templates_id CHECK (id <= 32766)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
