package main.database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) throws SQLException {
        var db = new DatabaseAPI();
//        System.out.println(db.inserirMembro("thi@email", "senha", "Jose", "jose@email", "111", PapelMembro.CLIENTE, "senha"));
//        System.out.println(db.inserirLivro("thi@email", "senha", "Percy", "1", "autor"));
        System.out.println(db.excluirMembro("thi@email", "senha", "jose@email"));
//        System.out.println(db.registrarEmprestimo("thi@email", "senha", "jose@email", "Percy"));
    }
}
