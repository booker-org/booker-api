CREATE TABLE authors (
  id         UUID         PRIMARY KEY DEFAULT uuidv7(),
  name       VARCHAR(255) NOT NULL,
  biography  TEXT,
  created_at TIMESTAMP    NOT NULL    DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP    NOT NULL    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_authors_name ON authors (name);