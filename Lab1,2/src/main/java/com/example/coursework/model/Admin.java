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
    //TODO This class is incomplete, You need to add attributes and constructors. Also override toString method
    private String phoneNum;

    public Admin(String login, String password, String name, String surname, String email) {
        super(login, password, name, surname, email);
    }
}
