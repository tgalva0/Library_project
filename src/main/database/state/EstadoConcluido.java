package main.database.state;

import main.database.Emprestimo;

public class EstadoConcluido implements EstadoEmprestimo {

    @Override
    public void processar(Emprestimo emprestimo) {
        System.out.println("O empréstimo foi concluído.");
    }

    @Override
    public String getNome() {
        return "concluído";
    }
}