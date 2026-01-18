package com.booker.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.booker.DTO.Book.BookCreateDTO;
import com.booker.DTO.Book.BookDTO;
import com.booker.DTO.Book.BookDetailDTO;
import com.booker.models.Book;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {
  private final AuthorMapper authorMapper;
  private final GenreMapper genreMapper;

  public Book toEntity(BookCreateDTO dto) {
    if (dto == null)
      return null;

    Book book = new Book();

    book.setTitle(dto.title());
    book.setSynopsis(dto.synopsis());
    book.setPageCount(dto.pageCount());

    return book;
  }

  public BookDTO toDTO(Book book) {
    if (book == null)
      return null;

    return new BookDTO(
      book.getId(),
      book.getTitle(),
      book.getSynopsis(),
      book.getPageCount(),
      book.getAuthor() != null ? book.getAuthor().getName() : null,
      book.getGenres().stream().map(g -> g.getName()).toList(),
      book.getCoverUrl(),
      book.getCreatedAt(),
      book.getUpdatedAt());
  }

  public BookDetailDTO toDetailDTO(Book book) {
    if (book == null)
      return null;

    return new BookDetailDTO(
      book.getId(),
      book.getTitle(),
      book.getSynopsis(),
      book.getPageCount(),
      authorMapper.toDTO(book.getAuthor()),
      genreMapper.toDTOList(book.getGenres().stream().toList()),
      book.getCoverUrl(),
      book.getCreatedAt(),
      book.getUpdatedAt());
  }

  public List<BookDTO> toDTOList(List<Book> books) {
    return books.stream()
      .map(this::toDTO)
      .toList();
  }

  public List<BookDetailDTO> toDetailDTOList(List<Book> books) {
    return books.stream()
      .map(this::toDetailDTO)
      .toList();
  }
}