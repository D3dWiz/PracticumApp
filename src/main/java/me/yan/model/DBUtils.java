package me.yan.model;

import java.sql.*;

public class DBUtils {

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            String jdbcURL = "jdbc:h2:/home/yan_/IdeaProjects/PracticumApp/Db/library";
            String jdbcUsername = "sa";
            String jdbcPassword = "";

            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static ResultSet getResultSet(String resultSetString) {
        try {
            Connection conn = DBUtils.getConnection();
            PreparedStatement state = conn.prepareStatement(resultSetString);
            return state.executeQuery();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
