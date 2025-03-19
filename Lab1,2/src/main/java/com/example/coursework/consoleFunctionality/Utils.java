package com.example.coursework.consoleFunctionality;

import com.example.coursework.model.Admin;
import com.example.coursework.model.Book;
import com.example.coursework.model.Client;
import com.example.coursework.model.User;
import com.example.coursework.model.Publication;
import com.example.coursework.model.Manga;
import com.example.coursework.model.enums.Genre;
import com.example.coursework.model.enums.Language;
import com.example.coursework.model.enums.Demographic;

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
                        System.out.println("Enter admin data: login;psw;name;surname;email;phone;");
                        line = scanner.nextLine();
                        String[] info = line.split(";");
                        
                        Admin admin = new Admin(info[0], info[1], info[2], info[3], info[4]);
                        admin.setPhoneNum(info[5]);
                        System.out.println(admin);
                        bookExchange.getAllUsers().add(admin);
                    } else {
                        System.out.println("No such user type");
                    }
                    break;
                case 2:
                    System.out.println("Enter user login");
                    var userLogin = scanner.nextLine();
                    for(User u: bookExchange.getAllUsers()){
                        if(u.getLogin().equals(userLogin)){
                            if(u instanceof Client client){
                                System.out.println("Enter updated client data: name;surname;email;address;YYYY-MM-DD;");
                                line = scanner.nextLine();
                                String[] info = line.split(";");
                                
                                client.setName(info[0]);
                                client.setSurname(info[1]);
                                client.setEmail(info[2]);
                                client.setAddress(info[3]);
                                client.setBirthDate(LocalDate.parse(info[4]));
                            } else if (u instanceof Admin admin) {
                                System.out.println("Enter updated admin data: name;surname;email;phone;");
                                line = scanner.nextLine();
                                String[] info = line.split(";");
                                
                                admin.setName(info[0]);
                                admin.setSurname(info[1]);
                                admin.setEmail(info[2]);
                                admin.setPhoneNum(info[3]);
                            }
                            System.out.println("User updated successfully!");
                            break;
                        }
                    }
                    break;
                case 3:
                    System.out.println("Enter user login to delete");
                    userLogin = scanner.nextLine();
                    bookExchange.getAllUsers().removeIf(u -> u.getLogin().equals(userLogin));
                    System.out.println("User deleted successfully!");
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
        var cmd = 0;
        while (cmd != 6) {
            System.out.println("""
                    Choose an option:
                    1 - create a publication
                    2 - update a publication
                    3 - delete a publication
                    4 - get all publications
                    5 - get publication
                    6 - return to main menu
                    """);

            cmd = scanner.nextInt();
            scanner.nextLine();

            switch (cmd) {
                case 1:
                    System.out.println("Which publication type? B/M");
                    var line = scanner.nextLine();
                    if (line.equals("B")) {
                        System.out.println("Enter book data: title;description;language;isbn;year;author;genre;");
                        line = scanner.nextLine();
                        String[] info = line.split(";");
                        
                        Book book = new Book(info[0], info[1], Language.valueOf(info[2]), info[3], 
                            Integer.parseInt(info[4]), info[5], Genre.valueOf(info[6]));
                        bookExchange.getAllPublications().add(book);
                        System.out.println(book);
                    } else if (line.equals("M")) {
                        System.out.println("Enter manga data: title;description;language;demographic;illustrator;volume;colored;");
                        line = scanner.nextLine();
                        String[] info = line.split(";");
                        
                        Manga manga = new Manga(info[0], info[1], Language.valueOf(info[2]), 
                            Demographic.valueOf(info[3]), info[4], Integer.parseInt(info[5]), Boolean.parseBoolean(info[6]));
                        bookExchange.getAllPublications().add(manga);
                        System.out.println(manga);
                    }
                    break;
                case 2:
                    System.out.println("Enter publication title");
                    var title = scanner.nextLine();
                    for(Publication p: bookExchange.getAllPublications()) {
                        if(p.getTitle().equals(title)) {
                            if(p instanceof Book book) {
                                System.out.println("Enter updated book data: description;language;isbn;year;author;genre;");
                                line = scanner.nextLine();
                                String[] info = line.split(";");
                                
                                book.setDescription(info[0]);
                                book.setLanguage(Language.valueOf(info[1]));
                                book.setIsbn(info[2]);
                                book.setYear(Integer.parseInt(info[3]));
                                book.setAuthors(info[4]);
                                book.setGenre(Genre.valueOf(info[5]));
                            } else if(p instanceof Manga manga) {
                                System.out.println("Enter updated manga data: description;language;demographic;illustrator;volume;colored;");
                                line = scanner.nextLine();
                                String[] info = line.split(";");
                                
                                manga.setDescription(info[0]);
                                manga.setLanguage(Language.valueOf(info[1]));
                                manga.setDemographic(Demographic.valueOf(info[2]));
                                manga.setIllustrator(info[3]);
                                manga.setVolume(Integer.parseInt(info[4]));
                                manga.setColored(Boolean.parseBoolean(info[5]));
                            }
                            System.out.println("Publication updated successfully!");
                            break;
                        }
                    }
                    break;
                case 3:
                    System.out.println("Enter publication title to delete");
                    title = scanner.nextLine();
                    bookExchange.getAllPublications().removeIf(p -> p.getTitle().equals(title));
                    System.out.println("Publication deleted successfully!");
                    break;
                case 4:
                    for(Publication p: bookExchange.getAllPublications()) {
                        System.out.println(p);
                    }
                    break;
                case 5:
                    System.out.println("Enter publication title");
                    title = scanner.nextLine();
                    for(Publication p: bookExchange.getAllPublications()) {
                        if(p.getTitle().equals(title)) {
                            System.out.println(p);
                            break;
                        }
                    }
                    break;
            }
        }
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

    public static List<User> getUsersFromFile() {
        List<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    int id = Integer.parseInt(parts[0]);
                    String login = parts[1];
                    
                    // Read the next line which contains user type and details
                    line = reader.readLine();
                    if (line != null) {
                        String[] userInfo = line.split(";");
                        if (userInfo[0].equals("C")) {
                            Client client = new Client(userInfo[1], userInfo[2], userInfo[3], userInfo[4], 
                                userInfo[5], userInfo[6], LocalDate.parse(userInfo[7]));
                            client.setId(id);
                            users.add(client);
                        } else if (userInfo[0].equals("A")) {
                            Admin admin = new Admin(userInfo[1], userInfo[2], userInfo[3], userInfo[4], userInfo[5]);
                            admin.setPhoneNum(userInfo[6]);
                            admin.setId(id);
                            users.add(admin);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return users;
    }
}
