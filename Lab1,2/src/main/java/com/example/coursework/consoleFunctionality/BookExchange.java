package com.example.coursework.consoleFunctionality;

import com.example.coursework.model.Publication;
import com.example.coursework.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookExchange {
    private List<Publication> allPublications;
    private List<User> allUsers;

    public BookExchange() {
        this.allPublications = new ArrayList<>();
        this.allUsers = new ArrayList<>();
    }
}
