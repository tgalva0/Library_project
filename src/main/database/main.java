package main.database;

import java.sql.SQLException;

public class main {
    public static void main(String[] args) throws SQLException {
        var app = new AppLoop();
        app.loop();

//        var db = new DatabaseAPI();
//        var ui = new TerminalUI();
//        System.out.println(db.inserirMembro("thi@email", "senha", "Jose", "jose@email", "111", PapelMembro.CLIENTE, "senha"));
//        System.out.println(db.inserirLivro("thi@email", "senha", "Jackson", "12", "autor"));
//        System.out.println(db.excluirMembro("thi@email", "senha", "jose@email"));
//        System.out.println(db.registrarEmprestimo( "jose@email", "Percy"));
//        System.out.println(db.excluirCopiasLivro("thi@email" ,"senha", "Percy", 2));
//        System.out.println(db.darBaixaEmprestimo("thi@email" ,"senha", "jose@email", "Percy"));
//        System.out.println(db.buscarLivroPorTitulo("Percy"));
//        System.out.println(db.buscarMembroPorEmail("thi@email"));
//        System.out.println(db.buscarEmprestimosPorEmail("jose@email"));
//        System.out.println(db.buscarLivrosPorAutor("autor"));
//        System.out.println(ui.renderMainHub(db.autenticarBibliotecario("thi@email", "senha")));

    }
}
