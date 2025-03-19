package com.example.coursework.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PublicationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User requester;

    @ManyToOne
    private Publication publication;

    private LocalDateTime requestedAt;
    private RequestStatus status;

    public enum RequestStatus {
        PENDING,
        ACCEPTED,
        DECLINED
    }

    public PublicationRequest(User requester, Publication publication) {
        this.requester = requester;
        this.publication = publication;
        this.requestedAt = LocalDateTime.now();
        this.status = RequestStatus.PENDING;
    }
} 