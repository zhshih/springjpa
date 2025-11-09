package com.example.springjpa.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    @Test
    void booksWithSameIdShouldBeEqual() {
        Book b1 = new Book(1, "Book A", "Description A");
        Book b2 = new Book(1, "Book A", "Description A");

        assertThat(b1).isEqualTo(b2);
    }
}
