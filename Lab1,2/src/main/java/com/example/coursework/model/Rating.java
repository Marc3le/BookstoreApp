package com.example.coursework.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Rating")
public class Rating extends Comment {
    private Integer stars = 0;  // Default value of 0 indicates no rating

    public Rating(String text, Client client, Publication publication, int stars) {
        super(text, client, publication);
        this.stars = Math.min(Math.max(stars, 1), 5);
    }

    @Override
    public String toString() {
        return String.format("Rating{stars=%d} %s", stars, super.toString());
    }
} 