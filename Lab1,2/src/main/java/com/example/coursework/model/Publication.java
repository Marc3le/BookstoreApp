package com.example.coursework.model;

import com.example.coursework.model.enums.Language;
import com.example.coursework.model.enums.PublicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    protected String title;
    protected String description;
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    protected List<Comment> commentList;
    protected LocalDate dateCreated;
    @Enumerated
    protected Language language;
    @Enumerated(EnumType.STRING)
    private PublicationStatus status = PublicationStatus.AVAILABLE;
    
    @ManyToOne
    private User owner;

    public Publication(String title, String description, Language language) {
        this.title = title;
        this.description = description;
        this.language = language;
        this.dateCreated = LocalDate.now();
        this.commentList = new ArrayList<>();
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public void setStatus(PublicationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return title + "(" + language + ")";
    }
}
