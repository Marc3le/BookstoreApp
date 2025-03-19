package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false, length = 1000)
    private String text;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private boolean isDeleted = false;
    
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> replies;
    
    @ManyToOne
    private Comment parentComment;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Publication publication;

    public Comment(String text, User user, Publication publication) {
        this.text = text;
        this.user = user;
        this.publication = publication;
        this.createdAt = LocalDateTime.now();
    }

    public Comment(String text, User user, Publication publication, Comment parentComment) {
        this.text = text;
        this.user = user;
        this.publication = publication;
        this.parentComment = parentComment;
        this.createdAt = LocalDateTime.now();
    }

    public void edit(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }
        this.text = newText.trim();
    }

    public void delete() {
        this.isDeleted = true;
        this.text = "[Deleted]";
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public String toString() {
        return String.format("Comment{id=%d, text='%s', createdAt=%s, user=%s, parentComment=%s}",
                id, text, createdAt, user != null ? user.getLogin() : "null",
                parentComment != null ? parentComment.getId() : "null");
    }
}
