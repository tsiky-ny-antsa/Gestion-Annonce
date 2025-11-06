package com.ffr.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_PATH = "ffr_stage.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                initializeDatabase();
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException {
        createTables();
        createDefaultUserIfNotExists();
    }

    private static void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username VARCHAR(50) NOT NULL UNIQUE, " +
            "password VARCHAR(100) NOT NULL, " +
            "role VARCHAR(10) NOT NULL DEFAULT 'user', " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";

        String createCategories = "CREATE TABLE IF NOT EXISTS categories (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name VARCHAR(100) NOT NULL UNIQUE, " +
            "description VARCHAR(255)" +
            ")";

        String createAnnonces = "CREATE TABLE IF NOT EXISTS annonces (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title VARCHAR(150) NOT NULL, " +
            "content TEXT, " +
            "category_id INTEGER, " +
            "audio_path VARCHAR(255), " +
            "prop VARCHAR(100), " +
            "type VARCHAR(50), " +
            "nbr_dif INTEGER DEFAULT 0, " +
            "nbr_prev INTEGER DEFAULT 0, " +
            "created_by INTEGER, " +
            "date_cre DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "date_upd DATETIME, " +
            "FOREIGN KEY (category_id) REFERENCES categories(id), " +
            "FOREIGN KEY (created_by) REFERENCES users(id)" +
            ")";

        String createProgramme = "CREATE TABLE IF NOT EXISTS programme (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "date_pro DATETIME, " +
            "dif1 BOOLEAN DEFAULT 0, " +
            "dif2 BOOLEAN DEFAULT 0, " +
            "dif3 BOOLEAN DEFAULT 0, " +
            "etat BOOLEAN DEFAULT 0, " +
            "annonce_id INTEGER, " +
            "nbr_dif INTEGER DEFAULT 0, " +
            "FOREIGN KEY (annonce_id) REFERENCES annonces(id)" +
            ")";

        stmt.execute(createUsers);
        stmt.execute(createCategories);
        stmt.execute(createAnnonces);
        stmt.execute(createProgramme);
        stmt.close();
    }

    private static void createDefaultUserIfNotExists() throws SQLException {
        Statement checkStmt = connection.createStatement();
        var rs = checkStmt.executeQuery("SELECT COUNT(*) as count FROM users WHERE username = 'admin'");
        
        boolean adminExists = false;
        if (rs.next()) {
            adminExists = rs.getInt("count") > 0;
        }
        rs.close();
        checkStmt.close();
        
        if (!adminExists) {
            Statement insertStmt = connection.createStatement();
            String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw("admin", org.mindrot.jbcrypt.BCrypt.gensalt());
            String insertAdmin = "INSERT INTO users (username, password, role) VALUES ('admin', '" + hashedPassword + "', 'root')";
            insertStmt.execute(insertAdmin);
            insertStmt.close();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
