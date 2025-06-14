package main.database;

import java.util.*;

public class TerminalUI {

    public Map<String, String> renderLoginInterface() {
        Scanner sc = new Scanner(System.in);
        System.out.println("!LOGIN!\nEMAIL: ");
        String email = sc.nextLine();
        System.out.println("\nPASSWORD: ");
        String password = sc.nextLine();

        // Criar o HashMap e armazenar os valores
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", password);

        return loginData;
    }

    public MainHubActions renderMainHub(Optional<Bibliotecario> bibliotecario) {
        int response1 = 0;
        if(bibliotecario.isPresent()) {
            do {
                System.out.println("\n\nLogged in: " + bibliotecario.get().getNome() +
                        "\n\nOpções:\n-Buscar por... (1)" +
                        "\n-Registrar/Devolver emprestimo (2)" +
                        "\n-Inserir novo... (3)" +
                        "\n-Remover... (4)" +
                        "\n-Atualizar registro de... (5)" +
                        "\n-LogOut (6)");
                try {
                    response1 = this.catchUserResponseInt();
                } catch (Exception e) {
                    System.out.println("Erro: numero inesperado informado: " + e.getMessage());
                }
            } while(response1 < 1 || response1 > 6);
            this.limparTela();
            System.out.println("\n\nLogged in: " + bibliotecario.get().getNome());
            int response2 = 0;
            switch (response1) {
                case 1 -> {
                    do {
                        System.out.println("\n\n!BUSCA!\n-Livro por Titulo (1)" +
                                "\n-Livro por Autor (2)" +
                                "\n-Membro por email (3)" +
                                "\n-Emprestimos por email (4)");
                        try {
                            response2 = this.catchUserResponseInt();
                        } catch (Exception e) {
                            System.out.println("Erro: numero inesperado informado: " + e.getMessage());
                        }
                    } while(response2 < 1 || response2 > 4);
                    this.limparTela();
                    System.out.println("\n\nLogged in: " + bibliotecario.get().getNome());
                    switch (response2) {
                        case 1 : return MainHubActions.LivroPorTitulo;
                        case 2 : return MainHubActions.LivroPorAutor;
                        case 3 : return MainHubActions.MembroPorEmail;
                        case 4 : return MainHubActions.EmprestimoPorEmail;
                    };
                    break;
                }
                case 2 -> {
                    do {
                        System.out.println("\n\n!Emprestimos!\n-Registrar emprestimo (1)" +
                                "\n-Devolução de emprestimo (2)");
                        try {
                            response2 = this.catchUserResponseInt();
                        } catch (Exception e) {
                            System.out.println("Erro: numero inesperado informado: " + e.getMessage());
                        }
                    } while(response2 < 1 || response2 > 2);
                    this.limparTela();
                    System.out.println("\n\nLogged in: " + bibliotecario.get().getNome());
                    switch (response2) {
                        case 1 : return MainHubActions.RegistrarEmprestimo;
                        case 2 : return MainHubActions.DevolverEmprestimo;
                    };
                    break;
                }
                case 3 -> {
                    do {
                        System.out.println("\n\n!Inserção!\n-Registrar Livro (1)" +
                                "\n-Registrar Membro (2)");
                        try {
                            response2 = this.catchUserResponseInt();
                        } catch (Exception e) {
                            System.out.println("Erro: numero inesperado informado: " + e.getMessage());
                        }
                    } while(response2 < 1 || response2 > 2);
                    this.limparTela();
                    System.out.println("\n\nLogged in: " + bibliotecario.get().getNome());
                    switch (response2) {
                        case 1 : return MainHubActions.RegistrarLivro;
                        case 2 : return MainHubActions.RegistrarMembro;
                    };
                    break;
                }
                case 4 -> {
                    do {
                        System.out.println("\n\n!Remoção!" +
                                "\n-Remover Copias de livro (1)" +
                                "\n-Remover Membro (2)");
                        try {
                            response2 = this.catchUserResponseInt();
                        } catch (Exception e) {
                            System.out.println("Erro: numero inesperado informado: " + e.getMessage());
                        }
                    } while(response2 < 1 || response2 > 2);
                    this.limparTela();
                    System.out.println("\n\nLogged in: " + bibliotecario.get().getNome());
                    switch (response2) {
                        case 1 : return MainHubActions.RemoverCopiasLivro;
                        case 2 : return MainHubActions.RemoverMembro;
                    };
                    break;
                }

                case 5 -> {
                    return MainHubActions.AtualizarMembro;
                }

                case 6 -> {
                    return MainHubActions.LogOut;
                }

                default -> {
                    return MainHubActions.ERRO;
                }
            }
        }
        System.out.println("\n\nERRO: Bibliotecario não logado.");

        return MainHubActions.ERRO;
    }

    public void renderMessage(String message) {
        System.out.println("\n"+message);
    }


    public void renderResultSearchLivro(Optional<List<Livro>> livros) {
//      imprime um livro vindo de uma pesquisa
        if(livros.isPresent()) {
            for (Livro livro : livros.get()) {
                System.out.println(livro);
            }
        } else {
            System.out.println("\nNenhum livro encontrado");;
        }
    }

    public void renderResultSerchAutor(Optional<List<Livro>> livros) {
        if(livros.isPresent()) {
            System.out.println("\nEncontrei os seguintes livros:\n\n");
            for(Livro livro : livros.get()) {
                System.out.println(livro + "\n\n");
            }
        } else {
            System.out.println("\nNenhum livro encontrado...");
        }
    }

    public void renderResultSerchEmprestimo(Optional<List<Emprestimo>> emprestimos) {
        if(emprestimos.isPresent()) {
            System.out.println("\nEncontrei os seguintes emprestimos:\n\n");
            for(Emprestimo emprestimo : emprestimos.get()) {
                System.out.println(emprestimo + "\n\n");
            }
        } else {
            System.out.println("\nNenhum emprestimo encontrado...");
        }
    }

    public void renderResultSerchMembros(Optional<Membro> membro)  {
        if(membro.isPresent()) {
            System.out.println("\nEncontrei o seguinte membro:\n\n");
            System.out.println(membro);
        } else {
            System.out.println("\nNenhum membro encontrado...");
        }
    }

    public Map<String, String> requestEmprestimoInfo() {
        this.renderMessage("Emprestimo: \nTitulo: ");
        var result = new HashMap<String, String>();
        String Titulo = this.catchUserResponseString();
        this.renderMessage("Email usuário: ");
        String Email = this.catchUserResponseString();
        result.put("titulo", Titulo);
        result.put("email", Email);
        return result;
    }

    public Livro requestLivroInfo() {
        this.renderMessage("Livro: \nTitulo: ");
        String titulo = this.catchUserResponseString();
        this.renderMessage("\nAutor: ");
        String autor = this.catchUserResponseString();
        this.renderMessage("\nISBN: ");
        String isbn = this.catchUserResponseString();
        return new Livro(titulo,isbn,1, autor);
    }

    public Membro requestMembroInfo() {
        this.renderMessage("Membro: \nNome: ");
        String nome = this.catchUserResponseString();
        this.renderMessage("\nEmail: ");
        String email = this.catchUserResponseString();
        this.renderMessage("\nTelefone: ");
        String telefone = this.catchUserResponseString();
        this.renderMessage("\nSenha: ");
        String senha = this.catchUserResponseString();
        this.renderMessage("\nPapel (Default = Cliente): ");
        String papel = this.catchUserResponseString();

        PapelMembro papelMembro = PapelMembro.CLIENTE;

        if(papel.equals("bibliotecario")) {
            papelMembro = PapelMembro.BIBLIOTECARIO;
        }


        return new Membro(nome,email,senha, telefone, papelMembro);
    }

    public int catchUserResponseInt() { //retorna inteiro digitado pelo usuario
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }

    public String catchUserResponseString() { //retorna a String digitada pelo usuario
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    public void limparTela() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.err.println("Erro ao limpar tela.");
        }
    }
}
