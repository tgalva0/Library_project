package main.database.state;

import main.database.Emprestimo;

public interface EstadoEmprestimo {
    void processar(Emprestimo emprestimo);
    String getNome();
}
