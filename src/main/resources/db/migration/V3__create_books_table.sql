CREATE TABLE books (
  id         UUID         PRIMARY KEY DEFAULT uuidv7(),
  title      VARCHAR(200) NOT NULL,
  synopsis   TEXT,
  page_count INT          NOT NULL,
  author_id  UUID         NOT NULL,
  cover_url  VARCHAR(2048),
  created_at TIMESTAMP    NOT NULL    DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP    NOT NULL    DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_books_author FOREIGN KEY (author_id)
    REFERENCES authors(id)
    ON DELETE CASCADE
);

CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author_id ON books(author_id);