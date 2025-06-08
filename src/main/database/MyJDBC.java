package main.database;

import java.sql.*;

public class MyJDBC {
    public static Connection getConnection() {

        String url = "jdbc:mysql://sql10.freesqldatabase.com:3306/sql10783720";
        String user = "sql10783720";
        String password = "w7DfdbBBCp";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexão estabelecida com sucesso!");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Falha ao estabelecer conexão.");
            return null;
        }
    }
}
