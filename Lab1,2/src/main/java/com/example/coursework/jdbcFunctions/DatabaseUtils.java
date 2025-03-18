package com.example.coursework.jdbcFunctions;

import com.example.coursework.model.Book;

import java.sql.*;

public class DatabaseUtils {

    public static Connection connectToDb() throws ClassNotFoundException {
        Connection conn = null;
        Class.forName("com.mysql.cj.jdbc.Driver");
        String DB_URL = "jdbc:mysql://localhost/lt_book_exchange";
        String USER = "root";
        String PASS = "";
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    public static void printClients() throws ClassNotFoundException, SQLException {
        Connection connection = connectToDb();
        Statement stmt = connection.createStatement();
        String query = "Select * from users";

    }

    public static void printUserByLogin(String login) throws ClassNotFoundException, SQLException {
        Connection connection = connectToDb();
        String query = "Select * from users WHERE `login` = ? ";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, login);
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            var id = rs.getInt(1);
            var loginDb = rs.getString("login");

            System.out.println(id + " " + loginDb);
        }


    }


//    public static void createBook(Book book) throws ClassNotFoundException {
//        Connection connection = connectToDb();
//        String query = "INSERT INTO publication (`title`, `description`, ...) VALUES (?,?,?,?) ";
//        PreparedStatement stmt = connection.prepareStatement(query);
//        //1.
//        stmt.setString(1, book.getTitle());
//        stmt.setString(2, book.getDescription());
//
//
//    }
}
