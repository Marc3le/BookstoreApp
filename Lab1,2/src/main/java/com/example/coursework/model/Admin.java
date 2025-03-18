package com.example.coursework.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Admin extends User{
    private String phoneNum;

    public Admin(String login, String password, String name, String surname, String email) {
        super(login, password, name, surname, email);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", login='" + getLogin() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", dateCreated=" + getDateCreated() +
                '}';
    }
}
