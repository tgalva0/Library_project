package main;

import Objects.*;
import main.database.DatabaseAPI;
import main.database.PapelMembro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AppLoop {
    private TerminalUI ui = new TerminalUI();
    private DatabaseAPI db = new DatabaseAPI();
    private Optional<Bibliotecario> login = Optional.empty();

    public void loop() {
        do{
            if(login.isPresent()) {
                MainHubActions response = ui.renderMainHub(login);
                switch (response) {
                    case LivroPorTitulo -> {
                        ui.renderMessage("Digite o Titulo do Livro: ");
                        String titulo = ui.catchUserResponseString();
                        ui.renderResultSearchLivro(db.buscarLivrosPorTitulo(titulo));
                        break;
                    }

                    case LivroPorAutor -> {
                        ui.renderMessage("Digite o nome do Autor: ");
                        ui.renderResultSerchAutor(db.buscarLivrosPorAutor(ui.catchUserResponseString()));
                        break;
                    }

                    case MembroPorEmail -> {
                        ui.renderMessage("Digite o email do Membro: ");
                        try {
                            ui.renderResultSerchMembros(db.buscarMembroPorEmail(ui.catchUserResponseString()));
                        } catch (Exception e) {
                            ui.renderMessage("Membro não encontrado");
                        }
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

                    case RegistrarLivro -> {
                        Livro novoLivro = ui.requestLivroInfo();
                        db.inserirLivro(novoLivro.getTitulo(), novoLivro.getIsbn(), novoLivro.getAutor());
                        break;
                    }

                    case RegistrarMembro -> {
                        Membro novoMembro = ui.requestMembroInfo();
                        db.inserirMembro(novoMembro.getNome(),novoMembro.getEmail(), novoMembro.getTelefone(), novoMembro.getPapel(), novoMembro.getSenha());
                        break;
                    }

                    case RemoverCopiasLivro -> {
                        ui.renderMessage("Digite o titulo do Livro: ");
                        String titulo = ui.catchUserResponseString();
                        ui.renderMessage("Digite o isbn do Livro: ");
                        String isbn = ui.catchUserResponseString();
                        ui.renderMessage("Quantas cópias deseja excluir?(Default = 1): ");
                        int quantidade = 1;
                        try {
                            quantidade = Integer.parseInt(ui.catchUserResponseString());
                        } catch (Exception e) {
                            ui.renderMessage("ERRO: Número não esperado digitado.");
                        }
                        db.excluirCopiasLivro(titulo, isbn, quantidade);
                    }

                    case RemoverMembro -> {
                        ui.renderMessage("Digite o email: ");
                        String email = ui.catchUserResponseString();
                        db.excluirMembro(email);
                    }

                    case AtualizarMembro -> {
                        ui.renderMessage("Email membro: ");
                        String email = ui.catchUserResponseString();
                        ui.renderMessage("Senha membro: ");
                        String senha = ui.catchUserResponseString();
                        if(db.validarLogin(email,senha)) {
                            ui.renderMessage("Usuário encontrado!\nDigite os novos dados do usuário:\n");
                            var novosDados = ui.requestMembroInfo();
                            db.atualizarDadosUsuario(email, senha,
                                   novosDados.getEmail(), novosDados.getSenha(), novosDados.getTelefone());
                        } else {
                            ui.renderMessage("Usuário não encontrado...");
                        }
                    }

                    case LogOut -> {
                        this.login = Optional.empty();
                    }

                    default -> throw new IllegalStateException("Unexpected value: " + response);
                }
            } else {
                Map<String, String> loginInfo = ui.renderLoginInterface();
                this.login = db.autenticarBibliotecario(loginInfo.get("email"), loginInfo.get("password"));
                String massage = login.isPresent() ? "Login OK" : "Login Failed";
                ui.renderMessage(massage);
            }

        }while(true);
    }
}
