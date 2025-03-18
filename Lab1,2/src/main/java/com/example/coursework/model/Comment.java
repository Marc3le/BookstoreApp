package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false, length = 1000)
    private String text;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> replies;
    
    @ManyToOne
    private Comment parentComment;
    
    @ManyToOne
    private Client client;
    
    @ManyToOne
    private Publication publication;

    public Comment(String text, Client client, Publication publication) {
        this.text = text;
        this.client = client;
        this.publication = publication;
        this.createdAt = LocalDateTime.now();
    }

    public Comment(String text, Client client, Publication publication, Comment parentComment) {
        this.text = text;
        this.client = client;
        this.publication = publication;
        this.parentComment = parentComment;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("Comment{id=%d, text='%s', createdAt=%s, client=%s, parentComment=%s}",
                id, text, createdAt, client != null ? client.getLogin() : "null",
                parentComment != null ? parentComment.getId() : "null");
    }
}
