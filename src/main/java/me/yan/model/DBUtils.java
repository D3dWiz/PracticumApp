package me.yan.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtils {
    private static final String JDBC_URL = "jdbc:h2:/home/yan_/IdeaProjects/PracticumApp/Db/library";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    public static ResultSet getResultSet(String resultSetString) {
        try {
            return getConnection().prepareStatement(resultSetString).executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
