package com.example.coursework.consoleFunctionality;

import com.example.coursework.model.Admin;
import com.example.coursework.model.Client;
import com.example.coursework.model.User;

import java.util.Scanner;

public class StartProgram {
    public static void main(String[] args) {
        //We create an object that allows us to work with console inputs
        Scanner scanner = new Scanner(System.in);
        //Because the goal is to read what the user entered into the console, we need to assign an initial value for cmd
        //In this case we use var keyword. If a value is provided, the type of variable is determined based on that value
        var cmd = "";

        //Before the menu generation, let's try to get data from file.
        //If it fails, just create a bookExchange object that has empty arrays of users and publications
        BookExchange bookExchange = Utils.readFromFile("myfile.txt");
        if (bookExchange == null) bookExchange = new BookExchange();

        //Primary menu
        while (!cmd.equals("q")) {
            System.out.println("""
                    Choose an option:
                    u - work with users
                    p - work with publications
                    q - quit
                    """);

            cmd = scanner.nextLine();
            switch (cmd) {
                case "u":
                    //Pass scanner and bookExchange
                    Utils.generateUserMenu(scanner, bookExchange);
                    break;
                case "p":
                    //Pass scanner and bookExchange
                    Utils.generatePublicationMenu(scanner, bookExchange);
                    break;
                case "q":
                    Utils.writeToFileAsObject("myfile.txt", bookExchange);
                    break;
                default:
                    System.out.println("No such command");
            }
        }

    }
}
