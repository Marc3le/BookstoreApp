package com.example.coursework.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public final class Client extends User {
    //TODO This class is incomplete, You need to add attributes and constructors. Also override toString method
    private String address;
    private LocalDate birthDate;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> myComments;

    public Client(String login, String password, String name, String surname, String email, String address, LocalDate birthDate) {
        super(login, password, name, surname, email);
        this.address = address;
        this.birthDate = birthDate;
    }
}
