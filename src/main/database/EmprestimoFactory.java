package main.database;

import java.sql.Timestamp;

public class EmprestimoFactory {

    // Método estático para criar um empréstimo
    public static Emprestimo criarEmprestimo(String titulo,
                                             String emailMembro,
                                             Timestamp dataEmprestimo,
                                             Timestamp dataDevolucao,
                                             String statusEmprestimo,
                                             double multa) {
        return new Emprestimo(titulo, emailMembro, dataEmprestimo, dataDevolucao, statusEmprestimo, multa);
    }
}