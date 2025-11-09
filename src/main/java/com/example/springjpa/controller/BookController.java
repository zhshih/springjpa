package com.example.springjpa.controller;

import com.example.springjpa.dto.ApiResponse;
import com.example.springjpa.dto.BookRequest;
import com.example.springjpa.dto.BookResponse;
import com.example.springjpa.mapper.BookMapper;
import com.example.springjpa.model.Book;
import com.example.springjpa.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookService.getAllBooks(pageable);

        Page<BookResponse> responsePage = bookPage.map(BookMapper::toResponse);

        return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", responsePage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable int id) {
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(ApiResponse.success("Book found", BookMapper.toResponse(book))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.failure("Book not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Validated @RequestBody BookRequest request) {
        Book savedBook = bookService.createBook(BookMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book created successfully", BookMapper.toResponse(savedBook)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable int id,
            @Validated @RequestBody BookRequest request) {
        return bookService.updateBook(id, BookMapper.toEntity(request))
                .map(updated -> ResponseEntity.ok(ApiResponse.success("Book updated successfully", BookMapper.toResponse(updated))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.failure("Book not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBook(@PathVariable int id) {
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("Book not found"));
        }
    }
}
