package main.database.state;

import main.database.Emprestimo;

public class EstadoAtrasado implements EstadoEmprestimo {

    @Override
    public void processar(Emprestimo emprestimo) {
        System.out.println("O empréstimo está atrasado.");
    }

    @Override
    public String getNome() {
        return "atrasado";
    }
}