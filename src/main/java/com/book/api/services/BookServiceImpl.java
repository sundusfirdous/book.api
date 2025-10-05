package com.book.api.services;

import com.book.api.dtos.BookDto;
import com.book.api.entities.Book;
import com.book.api.repositories.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    @Value("${project.images}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    private final BookRepository bookRepository;
    private final FileService fileService;

    public BookServiceImpl(BookRepository bookRepository, FileService fileService) {
        this.bookRepository = bookRepository;
        this.fileService = fileService;
    }

    @Override
    public BookDto addBook(BookDto bookDto, MultipartFile file) throws IOException {
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileAlreadyExistsException("File already exists! Please give another file!");
        }

        String uploadedFileName = fileService.uploadFile(path, file);
        String bookCoverUrl = baseUrl + "/api/v1/file/" + uploadedFileName;

        bookDto.setBookCover(uploadedFileName);
        bookDto.setBookCoverUrl(bookCoverUrl);

        Book book = convertToBook(bookDto);

        Book savedBook = bookRepository.save(book);

        return convertToBookDto(savedBook);
    }

    @Override
    public BookDto getBook(Long isbn) {
        Book book = bookRepository.findById(isbn).orElseThrow(() -> new RuntimeException("Book not found with isbn : " + isbn));
        return convertToBookDto(book);
    }

    @Override
    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(this::convertToBookDto)
                .toList();
    }

    @Override
    public BookDto updateBook(Long isbn, BookDto bookDto, MultipartFile file) throws IOException {
        Book book = bookRepository.findById(isbn).orElseThrow(() -> new RuntimeException("Book not found with isbn: " + isbn));

        String bookCover = book.getBookCover();
        String bookCoverUrl = book.getBookCoverUrl();
        if(file != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    Files.deleteIfExists(Paths.get(path + File.separator + book.getBookCover()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            bookCover = fileService.uploadFile(path, file);
            bookCoverUrl = baseUrl + "/api/v1/file/" + bookCover;
        }

        bookDto.setBookCover(bookCover);
        bookDto.setBookCoverUrl(bookCoverUrl);

        log.info("bookDto  = {}", bookDto);
        // update the book
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setPrice(bookDto.getPrice());
        book.setDescription(bookDto.getDescription());
        book.setCategory(bookDto.getCategory());
        book.setQuantity(bookDto.getQuantity());
        book.setBookCover(bookDto.getBookCover());
        book.setBookCoverUrl(bookDto.getBookCoverUrl());

        log.info("book  = {}", book);

        Book updatedBook = bookRepository.save(book);

        return convertToBookDto(updatedBook);
    }

    @Override
    public String deleteBook(Long isbn) throws IOException {
        Book book = bookRepository.findById(isbn).orElseThrow(() -> new RuntimeException("Book not found with isbn: " + isbn));
        Files.deleteIfExists(Paths.get(path + File.separator + book.getBookCover()));
        bookRepository.delete(book);
        return "Book Deleted successfully with isbn : " + isbn;
    }

    private BookDto convertToBookDto(Book book) {
        return BookDto.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .description(book.getDescription())
                .category(book.getCategory())
                .quantity(book.getQuantity())
                .bookCover(book.getBookCover())
                .bookCoverUrl(book.getBookCoverUrl())
                .build();
    }

    private Book convertToBook(BookDto bookDto) {
        return Book.builder()
                .isbn(bookDto.getIsbn())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .price(bookDto.getPrice())
                .description(bookDto.getDescription())
                .category(bookDto.getCategory())
                .quantity(bookDto.getQuantity())
                .bookCover(bookDto.getBookCover())
                .bookCoverUrl(bookDto.getBookCoverUrl())
                .build();
    }
}