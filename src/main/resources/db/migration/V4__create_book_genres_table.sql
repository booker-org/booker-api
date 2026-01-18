-- Tabela de junção para relacionamento Many-to-Many entre livros e gêneros
CREATE TABLE book_genres (
    book_id UUID NOT NULL,
    genre_id UUID NOT NULL,
    PRIMARY KEY (book_id, genre_id),
    CONSTRAINT fk_book_genres_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    CONSTRAINT fk_book_genres_genre FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);

CREATE INDEX idx_book_genres_book_id ON book_genres (book_id);

CREATE INDEX idx_book_genres_genre_id ON book_genres (genre_id);