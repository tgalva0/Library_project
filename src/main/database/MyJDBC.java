package main.database;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class MyJDBC {
    public static Connection getConnection() {

        Dotenv dotenv = Dotenv.configure().directory("C:/Thiago/BSI/3-Semestre/Eng_Soft2/Projeto_Final/bin/main/database/.env").load();
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

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
