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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public final class Client extends User {
    private String address;
    private LocalDate birthDate;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> myComments;

    public Client(String login, String password, String name, String surname, String email, String address, LocalDate birthDate) {
        super(login, password, name, surname, email);
        this.address = address;
        this.birthDate = birthDate;
        this.myComments = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", login='" + getLogin() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", address='" + address + '\'' +
                ", birthDate=" + birthDate +
                ", dateCreated=" + getDateCreated() +
                ", commentsCount=" + (myComments != null ? myComments.size() : 0) +
                '}';
    }
}
