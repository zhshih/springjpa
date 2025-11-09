package com.example.springjpa.mapper;


import com.example.springjpa.dto.BookRequest;
import com.example.springjpa.dto.BookResponse;
import com.example.springjpa.model.Book;

public class BookMapper {

    public static Book toEntity(BookRequest dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        return book;
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getDescription());
    }
}
