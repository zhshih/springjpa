package com.example.springjpa.controller;

import com.example.springjpa.model.Book;
import com.example.springjpa.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private Book book1;

    @BeforeEach
    void setUp() {
        book1 = new Book(1, "Book A", "Desc A");
    }

    @Test
    void shouldReturnAllBooksPaginated() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book1), pageable, 1);

        Mockito.when(bookService.getAllBooks(Mockito.any(Pageable.class))).thenReturn(bookPage);

        mockMvc.perform(get("/api/v1/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Books retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value(book1.getId()))
                .andExpect(jsonPath("$.data.content[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$.data.content[0].description").value(book1.getDescription()))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void shouldReturnBookByIdIfExists() throws Exception {
        Mockito.when(bookService.getBookById(1)).thenReturn(Optional.of(book1));

        mockMvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book found"))
                .andExpect(jsonPath("$.data.id").value(book1.getId()))
                .andExpect(jsonPath("$.data.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.data.description").value(book1.getDescription()));
    }

    @Test
    void shouldReturn404IfBookNotFound() throws Exception {
        Mockito.when(bookService.getBookById(2)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/books/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void shouldCreateBook() throws Exception {
        Mockito.when(bookService.createBook(Mockito.any(Book.class))).thenReturn(book1);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Book A\",\"description\":\"Desc A\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book created successfully"))
                .andExpect(jsonPath("$.data.id").value(book1.getId()))
                .andExpect(jsonPath("$.data.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.data.description").value(book1.getDescription()));
    }

    @Test
    void shouldUpdateBookIfExists() throws Exception {
        Book updatedBook = new Book(1, "Updated", "Updated Desc");
        Mockito.when(bookService.updateBook(Mockito.eq(1), Mockito.any(Book.class)))
                .thenReturn(Optional.of(updatedBook));

        mockMvc.perform(put("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"description\":\"Updated Desc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book updated successfully"))
                .andExpect(jsonPath("$.data.title").value("Updated"))
                .andExpect(jsonPath("$.data.description").value("Updated Desc"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingBook() throws Exception {
        Mockito.when(bookService.updateBook(Mockito.eq(2), Mockito.any(Book.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/books/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"description\":\"Updated Desc\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void shouldDeleteBookIfExists() throws Exception {
        Mockito.when(bookService.deleteBook(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingBook() throws Exception {
        Mockito.when(bookService.deleteBook(2)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/books/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Book not found"));
    }
}
