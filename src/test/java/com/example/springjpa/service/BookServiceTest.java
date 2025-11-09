package com.example.springjpa.service;

import com.example.springjpa.model.Book;
import com.example.springjpa.repository.BookRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookService bookService;
    private SimpleMeterRegistry meterRegistry;

    private Book book1;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        bookService = new BookService(bookRepository, meterRegistry);
        book1 = new Book(1, "Book A", "Desc A");
    }

    @Test
    void shouldReturnAllBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book1), pageable, 1);

        Mockito.when(bookRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(bookPage);

        Page<Book> result = bookService.getAllBooks(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1).contains(book1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);

        assertThat(meterRegistry
                .counter("book.getAllBooks.calls")
                .count())
                .isEqualTo(1.0);
    }

    @Test
    void shouldReturnBookByIdIfExists() {
        Mockito.when(bookRepository.findById(1)).thenReturn(Optional.of(book1));

        Optional<Book> book = bookService.getBookById(1);

        assertThat(book).isPresent().contains(book1);

        assertThat(meterRegistry
                .counter("book.getBookById.calls")
                .count())
                .isEqualTo(1.0);
    }

    @Test
    void shouldReturnEmptyIfBookDoesNotExist() {
        Mockito.when(bookRepository.findById(2)).thenReturn(Optional.empty());

        Optional<Book> book = bookService.getBookById(2);

        assertThat(book).isEmpty();

        assertThat(meterRegistry
                .counter("book.getBookById.calls")
                .count())
                .isEqualTo(1.0);
    }

    @Test
    void shouldCreateBook() {
        Mockito.when(bookRepository.save(book1)).thenReturn(book1);

        Book saved = bookService.createBook(book1);

        assertThat(saved).isEqualTo(book1);

        assertThat(meterRegistry
                .counter("book.createBook.calls")
                .count())
                .isEqualTo(1.0);
    }

    @Test
    void shouldUpdateBookIfExists() {
        Book updatedDetails = new Book();
        updatedDetails.setTitle("Updated");
        updatedDetails.setDescription("Updated Desc");

        Mockito.when(bookRepository.findById(1)).thenReturn(Optional.of(book1));
        Mockito.when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Book> updated = bookService.updateBook(1, updatedDetails);

        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("Updated");
        assertThat(updated.get().getDescription()).isEqualTo("Updated Desc");

        assertThat(meterRegistry
                .counter("book.updateBook.calls")
                .count())
                .isEqualTo(1.0);
    }

    @Test
    void shouldReturnEmptyWhenUpdatingNonExistingBook() {
        Mockito.when(bookRepository.findById(2)).thenReturn(Optional.empty());

        Optional<Book> updated = bookService.updateBook(2, new Book());

        assertThat(updated).isEmpty();

        assertThat(meterRegistry
                .counter("book.updateBook.calls")
                .count())
                .isEqualTo(1.0);
    }

    @Test
    void shouldDeleteBookIfExists() {
        Mockito.when(bookRepository.findById(1)).thenReturn(Optional.of(book1));

        boolean deleted = bookService.deleteBook(1);

        assertThat(deleted).isTrue();
        Mockito.verify(bookRepository).deleteById(1);

        assertThat(meterRegistry
                .counter("book.deleteBook.calls")
                .count())
                .isEqualTo(1.0);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistingBook() {
        Mockito.when(bookRepository.findById(2)).thenReturn(Optional.empty());

        boolean deleted = bookService.deleteBook(2);

        assertThat(deleted).isFalse();
        Mockito.verify(bookRepository, Mockito.never()).deleteById(Mockito.anyInt());

        assertThat(meterRegistry
                .counter("book.deleteBook.calls")
                .count())
                .isEqualTo(1.0);
    }
}
