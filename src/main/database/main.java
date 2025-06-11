package main.database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) throws SQLException {
        var db = new DatabaseAPI();
        System.out.println(db.inserirMembro("thi@email", "senha", "Thiago", "thi@email", "119", PapelMembro.BIBLIOTECARIO, "senha"));
        System.out.println(db.inserirLivro("thi@email", "senha", "Percy", "1", "autor"));
    }
}
