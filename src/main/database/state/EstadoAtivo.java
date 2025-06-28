package main.database.state;
import main.database.Emprestimo;

public class EstadoAtivo implements EstadoEmprestimo {
    @Override
    public void processar(Emprestimo e) {
        // Exemplo: checar se está vencido e mudar para atrasado, se necessário.
    }

    @Override
    public String getNome() {
        return "ativo";
    }
}
