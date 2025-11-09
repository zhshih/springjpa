package com.example.springjpa.repository;

import com.example.springjpa.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    Book findByDescription(String description);

    @Query(value = "SELECT * FROM books WHERE LOWER(title) Like %:keyword%", nativeQuery = true)
    List<Book> findByTitle(String title);
}
