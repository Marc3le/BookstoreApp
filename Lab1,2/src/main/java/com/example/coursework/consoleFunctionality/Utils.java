package com.example.coursework.consoleFunctionality;

import com.example.coursework.model.Admin;
import com.example.coursework.model.Book;
import com.example.coursework.model.Client;
import com.example.coursework.model.User;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    public static void generateUserMenu(Scanner scanner, BookExchange bookExchange) {

        //In this case cmd is an int
        var cmd = 0;
        while (cmd != 6) {
            System.out.println("""
                    Choose an option:
                    1 - create a user
                    2 - update a user
                    3 - delete a user
                    4 - get all users
                    5 - get user
                    6 - return to main menu
                    """);

            cmd = scanner.nextInt(); //nextInt doesn't have a next line symbol, so sequent line is necessary to not have any issues
            scanner.nextLine();

            switch (cmd) {
                case 1:
                    System.out.println("Which user type? C/A");
                    var line = scanner.nextLine();
                    if (line.equals("C")) {
                        System.out.println("Enter user data: login;psw;name;surname;email;address;YYYY-MM-DD;");
                        line = scanner.nextLine();

                        String[] info = line.split(";");

                        Client client = new Client(info[0], info[1], info[2], info[3], info[4], info[6], LocalDate.parse(info[5]));
                        System.out.println(client);
                        //Add this user to user list
                        bookExchange.getAllUsers().add(client);
                    } else if (line.equals("A")) {
                        //TODO Admin creation
                    } else {
                        System.out.println("No such user type");
                    }
                    break;
                case 2:
                    System.out.println("Enter user login");
                    var userLogin = scanner.nextLine();
                    for(User u: bookExchange.getAllUsers()){
                        if(u.getLogin().equals(userLogin)){
                            //TODO ask user for update info and then update
                            if(u instanceof Client client){

                            }else if (u instanceof Admin admin){

                            }
                        }
                    }
                    break;
                case 3:
                    //TODO find a user by login and then just remove
                    //bookExchange.getAllUsers().remove(user);
                    break;
                case 4:
                    for(User u: bookExchange.getAllUsers()){
                        System.out.println(u);
                    }
                    //User print using lambdas
                    //bookExchange.getAllUsers().forEach(u-> System.out.println(u));
                    break;
            }
        }
    }

    public static void generatePublicationMenu(Scanner scanner, BookExchange bookExchange) {
        //TODO create a menu for publications. Very similar to users
    }

    public static void writeToFileAsObject(String fileName, BookExchange bookExchange) {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            objectOutputStream.writeObject(bookExchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static BookExchange readFromFile(String fileName) {
        BookExchange bookExchange = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            //We must specify the type of data that is in the file, because the program doesn't know
            bookExchange = (BookExchange) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return bookExchange;
    }

    public static void writeUsersToFile(List<User> userList) {
        try (FileWriter fileWriter = new FileWriter("users.txt")) {

            for (User u : userList) {
                fileWriter.write(u.getId() + ":" + u.getLogin() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> getUsersFromFile(){
        //Initialize List
        List<User> users = new ArrayList<>();

        //TODO logic to read line by line and create CLient/Admin objects

        //users.add(client/admin)
        return users;
    }
}
