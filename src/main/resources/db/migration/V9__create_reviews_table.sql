CREATE TABLE reviews (
  id          UUID          PRIMARY KEY DEFAULT uuidv7(),
  score       NUMERIC(2, 1) NOT NULL    CHECK (score >= 0.0 AND score <= 5.0),
  headline    VARCHAR(50),
  text        TEXT          NOT NULL    CHECK (length(text) > 0 AND length(text) <= 2048),
  like_count  INTEGER       NOT NULL    CHECK (like_count >= 0) DEFAULT 0,

  created_at  TIMESTAMP     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP     NOT NULL    DEFAULT CURRENT_TIMESTAMP,

  user_id     UUID          NOT NULL,
  book_id     UUID          NOT NULL,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
  UNIQUE (user_id, book_id)
);

CREATE INDEX idx_reviews_book_id ON reviews(book_id);