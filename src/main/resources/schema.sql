CREATE TABLE IF NOT EXISTS ff_app_session (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    token_hash CHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ff_app_session_user (id_usuario),
    INDEX idx_ff_app_session_expiry (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ff_app_user_state (
    id_usuario INT NOT NULL PRIMARY KEY,
    state_json LONGTEXT NOT NULL,
    version BIGINT NOT NULL DEFAULT 1,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ff_app_campaign (
    campaign_id VARCHAR(64) NOT NULL PRIMARY KEY,
    invite_code VARCHAR(12) NOT NULL UNIQUE,
    owner_user_id INT NOT NULL,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    state_json LONGTEXT NOT NULL,
    version BIGINT NOT NULL DEFAULT 1,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ff_app_campaign_owner (owner_user_id),
    INDEX idx_ff_app_campaign_public (is_private)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ff_app_campaign_member (
    campaign_id VARCHAR(64) NOT NULL,
    id_usuario INT NOT NULL,
    role_name VARCHAR(16) NOT NULL DEFAULT 'player',
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (campaign_id, id_usuario),
    INDEX idx_ff_app_campaign_member_user (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
