package com.example.dbdemo;

import org.sqlite.JDBC;

import java.sql.*;


import static java.lang.Class.forName;

public class DatabaseHelper {

    private static final String DATABASE_LOCATION = DatabaseHelper.class.getResource("/com/example/dbdemo/database/test-db.db").toExternalForm();

    public static boolean isDatabaseReady() {
        boolean isDriverReady = checkDriver();
        if (!isDriverReady) return false;

        boolean isConnectionReady = checkConnection();
        if (!isConnectionReady) return false;

        boolean isTableReady = checkTables();
        if (!isTableReady) return false;

        return true;

        // return !checkConnection() && !checkDriver() && checkTables();
    }

    public static boolean checkDriver() {
        try {

            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new JDBC());
            System.out.println("Driver Registered successfully!");
            return true;


        } catch (Exception e) {
            System.out.println("Driver not loaded! Reason: " + e.getMessage());
            return false;
        }
    }

    private static boolean checkConnection() {
        Connection connection = connect();

        if (connection != connect()) {
            System.out.println("Database connected");
            return true;

        }

        return false;
    }


    private static boolean checkTables() {
        String requiredTable = "test_tbl";

        String checkTablesQuery = "SELECT DISTINCT tbl_name FROM sqlite_master";

        Connection connection = connect();
        try {
            //Establish Database connection


            if (connection != null) {
                //Prepare the Statement

                PreparedStatement preparedStatement = connection.prepareStatement(checkTablesQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String tableName = resultSet.getString("tbl_name");
                    if (tableName.equalsIgnoreCase(requiredTable)) {
                        return true;
                    }

                }
            }


        } catch (Exception e) {
            System.out.println("Could not find table. Reason:" + e.getMessage());
        }
//        System.out.println("Table not found");
        return createTable(connection);
    }

    private static boolean createTable(Connection connection) {
        //1. Prepare a query to create a table
        String CreateTableQuery = "CREATE TABLE test_tbl (id INTEGER PRIMARY KEY, name VARCHAR(100)) ";
        try {

            if (connection == null) connection = connect();

            if (connection != null) {
                //2. Create sql statement
                Statement statement = connection.createStatement();

                //3. Execute the statement
                statement.execute(CreateTableQuery);
                System.out.println("Table created successfully");
                return true;
            }


        } catch (SQLException sqlException) {
            System.out.println("Failed to create table. Reason: " + sqlException.getMessage());
        }
        return false;
    }


    public static Connection connect() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_LOCATION);
//            System.out.println("Database Connected Successfully");
            return connection;
        } catch (Exception e) {
            System.out.println("Connection failed! Reason:" + e.getMessage());
            return null;
        }
    }
}
