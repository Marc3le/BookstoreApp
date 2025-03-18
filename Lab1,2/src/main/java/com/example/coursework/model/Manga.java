package com.example.coursework.model;

import com.example.coursework.model.enums.Demographic;
import com.example.coursework.model.enums.Language;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Manga extends Publication{
    @Enumerated
    private Demographic demographic;
    private String illustrator;
    private int volume;
    private boolean colored;

    public Manga(String title, String description, Language language, Demographic demographic, String illustrator, int volume, boolean colored) {
        super(title, description, language);
        this.demographic = demographic;
        this.illustrator = illustrator;
        this.volume = volume;
        this.colored = colored;
    }
}
