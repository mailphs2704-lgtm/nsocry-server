-- V003: read model SKILL tĩnh, chuẩn hóa khỏi JSON và gameplay entity.

CREATE TABLE client_skill_options (
    id TINYINT UNSIGNED NOT NULL,
    name VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_client_skill_options_id CHECK (id <= 126)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE client_skill_classes (
    id TINYINT UNSIGNED NOT NULL,
    name VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_client_skill_classes_id CHECK (id <= 254)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE client_skill_templates (
    id TINYINT UNSIGNED NOT NULL,
    class_id TINYINT UNSIGNED NOT NULL,
    sort_order TINYINT UNSIGNED NOT NULL,
    name VARCHAR(500) NOT NULL,
    max_point TINYINT NOT NULL,
    type TINYINT NOT NULL,
    icon_id SMALLINT NOT NULL,
    description VARCHAR(5000) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_client_skill_templates_id CHECK (id <= 127),
    CONSTRAINT chk_client_skill_templates_order CHECK (sort_order <= 126),
    CONSTRAINT uq_client_skill_template_order UNIQUE (class_id, sort_order),
    CONSTRAINT fk_client_skill_template_class FOREIGN KEY (class_id)
        REFERENCES client_skill_classes (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE client_skill_levels (
    id SMALLINT UNSIGNED NOT NULL,
    template_id TINYINT UNSIGNED NOT NULL,
    sort_order TINYINT UNSIGNED NOT NULL,
    point TINYINT NOT NULL,
    required_level TINYINT NOT NULL,
    mana_use SMALLINT NOT NULL,
    cooldown INT NOT NULL,
    dx SMALLINT NOT NULL,
    dy SMALLINT NOT NULL,
    max_fight TINYINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_client_skill_levels_id CHECK (id <= 32767),
    CONSTRAINT chk_client_skill_levels_order CHECK (sort_order <= 126),
    CONSTRAINT uq_client_skill_level_order UNIQUE (template_id, sort_order),
    CONSTRAINT fk_client_skill_level_template FOREIGN KEY (template_id)
        REFERENCES client_skill_templates (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE client_skill_level_options (
    skill_level_id SMALLINT UNSIGNED NOT NULL,
    sort_order TINYINT UNSIGNED NOT NULL,
    parameter_value SMALLINT NOT NULL,
    option_template_id TINYINT UNSIGNED NOT NULL,
    PRIMARY KEY (skill_level_id, sort_order),
    CONSTRAINT chk_client_skill_level_options_order CHECK (sort_order <= 126),
    CONSTRAINT fk_client_skill_level_option_level FOREIGN KEY (skill_level_id)
        REFERENCES client_skill_levels (id),
    CONSTRAINT fk_client_skill_level_option_template FOREIGN KEY (option_template_id)
        REFERENCES client_skill_options (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
