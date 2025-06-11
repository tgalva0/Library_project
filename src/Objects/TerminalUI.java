package Objects;

import main.database.DatabaseAPI;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class TerminalUI {
    public void renderMainHub() {
//        imprimir o hub de opções do usuario
    }

    public void renderSerchLivro(ResultSet rs) throws SQLException {
//        imprime o resultado de uma pesquisa por livros no slq
//        (ainda preciso criar um metodo para isso na classe databaseAPI)
    }

    public void renderSerchAutor(ResultSet rs) throws SQLException {
//        mesma coisa do metodo anterior só que para autores
//        (ainda preciso criar um metodo para isso na classe databaseAPI)
    }

    public void renderSerchEmprestimo(ResultSet rs) throws SQLException {
//        mesma coisa do metodo anterior só que para emprestimos
//        (ainda preciso criar um metodo para isso na classe databaseAPI)
    }
    public void renderSerchMembros(ResultSet rs) throws SQLException {
//        mesma coisa do metodo anterior só que para membros
//        (ainda preciso criar um metodo para isso na classe databaseAPI)
    }

    public int catchUserResponseInt() { //retorna inteiro digitado pelo usuario
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }

    public String catchUserResponseString() { //retorna a String digitada pelo usuario
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }
}
