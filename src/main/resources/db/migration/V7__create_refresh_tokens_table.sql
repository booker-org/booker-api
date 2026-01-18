CREATE TABLE refresh_tokens (
  id          UUID         PRIMARY KEY DEFAULT uuidv7(),
  token_hash  VARCHAR(255) NOT NULL UNIQUE,
  user_id     UUID         NOT NULL,
  expires_at  TIMESTAMP    NOT NULL,
  revoked     BOOLEAN      NOT NULL    DEFAULT FALSE,
  revoked_at  TIMESTAMP,
  device_info VARCHAR(500),
  ip_address  VARCHAR(45),
  created_at  TIMESTAMP    NOT NULL    DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);

CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);

CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);