package main.database;

import java.sql.Connection;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) throws SQLException {
        var db = new DatabaseAPI();

//        var sucesso = db.atualizarDadosUsuario("thiago@email.com", "senhaSegura123", "jose@email.com",
//                "senhaSegura321", "jose2@email.com", "senhaSegura654", "119");
//
//
//        if(sucesso) {
//            System.out.println("Alteração realizada com sucesso!");
//        } else {
//            System.out.println("Erro ao alterar dados de usuário");
//        }

        System.out.println(db.validarLogin("thi@email", "SnhaSegura123"));
    }
}
