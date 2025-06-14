package main;

import Objects.Bibliotecario;
import Objects.MainHubActions;
import Objects.TerminalUI;
import main.database.DatabaseAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AppLoop {
    private TerminalUI ui = new TerminalUI();
    private DatabaseAPI db = new DatabaseAPI();
    private Optional<Bibliotecario> login = Optional.empty();

    public void loop() {
        int i = 0;
        do{
            if(login.isPresent()) {
                MainHubActions response = ui.renderMainHub(login);
                switch (response) {
                    case LivroPorTitulo -> {
                        ui.renderMessage("Digite o Titulo do Livro: ");
                        ui.renderResultSearchLivro(db.buscarLivroPorTitulo(ui.catchUserResponseString()));
                        break;
                    }

                    case LivroPorAutor -> {
                        ui.renderMessage("Digite o nome do Autor: ");
                        ui.renderResultSerchAutor(db.buscarLivrosPorAutor(ui.catchUserResponseString()));
                        break;
                    }

                    case MembroPorEmail -> {
                        ui.renderMessage("Digite o email do Membro: ");
                        ui.renderResultSerchMembros(db.buscarMembroPorEmail(ui.catchUserResponseString()));
                        break;
                    }

                    case EmprestimoPorEmail -> {
                        ui.renderMessage("Digite o email atrelado ao Emprestimo: ");
                        ui.renderResultSerchEmprestimo(db.buscarEmprestimosPorEmail(ui.catchUserResponseString()));
                        break;
                    }

                    case RegistrarEmprestimo -> {
                        //pegar o hashmap da ui e registrar o emprestimo
                        Map<String, String> getInfo = ui.requestEmprestimoInfo();
                        db.registrarEmprestimo(getInfo.get("email"), getInfo.get("titulo"));
                        break;
                    }

                    case DevolverEmprestimo -> {
                        Map<String, String> getInfo = ui.requestEmprestimoInfo();
                        db.darBaixaEmprestimo(getInfo.get("email"), getInfo.get("titulo"));
                        break;
                    }
                }
                i++;
            } else {
                Map<String, String> loginInfo = ui.renderLoginInterface();
                this.login = db.autenticarBibliotecario(loginInfo.get("email"), loginInfo.get("password"));
                String massage = login.isPresent() ? "Login OK" : "Login Failed";
                ui.renderMessage(massage);
            }

        }while(i < 5);
    }
}
