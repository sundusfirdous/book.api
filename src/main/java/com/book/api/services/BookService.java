package com.book.api.services;

import com.book.api.dtos.BookDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BookService {

    BookDto addBook(BookDto bookDto, MultipartFile file) throws IOException;

    BookDto getBook(Long isbn);

    List<BookDto> getAllBooks();

    BookDto updateBook(Long isbn, BookDto bookDto, MultipartFile file) throws IOException;

    String deleteBook(Long isbn) throws IOException;
}