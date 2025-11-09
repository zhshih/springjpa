package com.example.springjpa.service;

import com.example.springjpa.model.Book;
import com.example.springjpa.repository.BookRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;
    private final MeterRegistry meterRegistry;

    @Autowired
    public BookService(BookRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    @CircuitBreaker(name = "bookService", fallbackMethod = "getAllBooksFallback")
    @Retry(name = "bookService")
    public Page<Book> getAllBooks(Pageable pageable) {
        meterRegistry.counter("book.getAllBooks.calls").increment();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return repository.findAll(pageable);
        } finally {
            sample.stop(meterRegistry.timer("book.getAllBooks.duration"));
        }
    }

    @CircuitBreaker(name = "bookService", fallbackMethod = "getBookByIdFallback")
    @Retry(name = "bookService")
    public Optional<Book> getBookById(int id) {
        meterRegistry.counter("book.getBookById.calls").increment();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return repository.findById(id);
        } finally {
            sample.stop(meterRegistry.timer("book.getBookById.duration"));
        }
    }

    @CircuitBreaker(name = "bookService", fallbackMethod = "createBookFallback")
    @Retry(name = "bookService")
    public Book createBook(Book book) {
        meterRegistry.counter("book.createBook.calls").increment();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return repository.save(book);
        } finally {
            sample.stop(meterRegistry.timer("book.createBook.duration"));
        }
    }

    @CircuitBreaker(name = "bookService", fallbackMethod = "updateBookFallback")
    @Retry(name = "bookService")
    public Optional<Book> updateBook(int id, Book bookDetails) {
        meterRegistry.counter("book.updateBook.calls").increment();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return repository.findById(id)
                    .map(existingBook -> {
                        existingBook.setTitle(bookDetails.getTitle());
                        existingBook.setDescription(bookDetails.getDescription());
                        return repository.save(existingBook);
                    });
        } finally {
            sample.stop(meterRegistry.timer("book.updateBook.duration")); // stop timing
        }
    }

    @CircuitBreaker(name = "bookService", fallbackMethod = "deleteBookFallback")
    @Retry(name = "bookService")
    public boolean deleteBook(int id) {
        meterRegistry.counter("book.deleteBook.calls").increment();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Optional<Book> book = repository.findById(id);
            if (book.isPresent()) {
                repository.deleteById(id);
                return true;
            }
            return false;
        } finally {
            sample.stop(meterRegistry.timer("book.deleteBook.duration")); // stop timing
        }
    }

    private Page<Book> getAllBooksFallback(Pageable pageable, Throwable t) {
        return Page.empty(pageable);
    }

    private Optional<Book> getBookByIdFallback(int id, Throwable t) {
        return Optional.empty();
    }

    private Book createBookFallback(Book book, Throwable t) {
        throw new RuntimeException("Unable to create book at this time", t);
    }

    private Optional<Book> updateBookFallback(int id, Book bookDetails, Throwable t) {
        return Optional.empty();
    }

    private boolean deleteBookFallback(int id, Throwable t) {
        return false;
    }
}
