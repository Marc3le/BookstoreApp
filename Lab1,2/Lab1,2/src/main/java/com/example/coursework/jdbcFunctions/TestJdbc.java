package com.example.coursework.jdbcFunctions;

import java.sql.SQLException;

public class TestJdbc {
    public static void main(String[] args) {
        try {
            DatabaseUtils.printClients();
            System.out.println("--------------------");
            //DatabaseUtils.printUserByLogin("client1");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
