package com.example.coursework.model;

import com.example.coursework.model.enums.Genre;
import com.example.coursework.model.enums.Language;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book extends Publication{
    private String isbn;
    private int year;
    private String authors;
    @Enumerated
    private Genre genre;

    public Book(String title, String description, Language language, String isbn, int year, String authors, Genre genre) {
        super(title, description, language);
        this.isbn = isbn;
        this.year = year;
        this.authors = authors;
        this.genre = genre;
    }
}
