ALTER TABLE books
  ADD COLUMN release_year  SMALLINT       NOT NULL,
  ADD COLUMN rating_sum    DECIMAL(10, 1) DEFAULT 0.0,
  ADD COLUMN ratings_count INTEGER        DEFAULT 0
;