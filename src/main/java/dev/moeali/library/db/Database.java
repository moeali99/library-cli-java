package dev.moeali.library.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final String dbPath;
    private Connection connection;
    private DSLContext dsl;

    public Database(String dbPath) {
        this.dbPath = dbPath;
    }

    public void connect() {
        try {
            Path p = Path.of(dbPath).toAbsolutePath();
            Files.createDirectories(p.getParent());
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + p);
            this.dsl = DSL.using(this.connection, SQLDialect.SQLITE);
            initSchema();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to SQLite at " + dbPath, e);
        }
    }

    private void initSchema() {
        // Minimal schema using jOOQ DSL (no codegen)
        dsl.query("CREATE TABLE IF NOT EXISTS BOOK (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TITLE TEXT NOT NULL," +
                "AUTHOR TEXT NOT NULL," +
                "AVAILABLE INTEGER NOT NULL DEFAULT 1" +
                ")").execute();

        dsl.query("CREATE TABLE IF NOT EXISTS LOAN (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "BOOK_ID INTEGER NOT NULL," +
                "USER_NAME TEXT NOT NULL," +
                "LOAN_DATE TEXT NOT NULL," +
                "RETURN_DATE TEXT," +
                "FOREIGN KEY (BOOK_ID) REFERENCES BOOK(ID)" +
                ")").execute();
    }

    public DSLContext dsl() {
        if (dsl == null) connect();
        return dsl;
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException ignore) { }
    }
}


