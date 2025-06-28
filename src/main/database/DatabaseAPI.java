package main.database;

import main.database.strategy.CalculadoraMulta;
import main.database.strategy.MultaBasica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseAPI {
    Connection conexao = MyJDBC.getConnection();

    public Optional<List<Livro>> buscarLivrosPorTituloInicial(String prefixo) {
        String sql = """
        SELECT 
            l.id_livro,
            l.titulo,
            l.isbn,
            a.Nome AS nome_autor,
            COUNT(c.id_copia_livro) AS quantidade_copias
        FROM livro l
        JOIN autor a ON l.id_autor = a.id_autor
        LEFT JOIN copia_livro c ON l.id_livro = c.id_livro
        WHERE l.titulo LIKE ?
        GROUP BY l.id_livro, l.titulo, a.nome
    """;

        try (Connection conn = MyJDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, prefixo + "%");
            ResultSet rs = stmt.executeQuery();

            List<Livro> livros = new ArrayList<>();

            while (rs.next()) {
                Livro livro = new Livro(
                        rs.getString("titulo"),
                        rs.getString("isbn"),
                        rs.getInt("quantidade_copias"),
                        rs.getString("nome_autor")
                );
                livros.add(livro);
            }

            return livros.isEmpty() ? Optional.empty() : Optional.of(livros);

        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    public Optional<List<Livro>> buscarLivrosPorAutor(String nomeAutor) {
        List<Livro> livros = new ArrayList<>();

        try {
            // Buscar livros de autores cujo nome começa com o prefixo informado
            String sqlLivros = """
            SELECT l.id_livro, l.titulo, l.isbn, a.nome AS autor
            FROM livro l
            JOIN autor a ON l.id_autor = a.id_autor
            WHERE a.nome LIKE ?
        """;

            PreparedStatement stmtLivros = conexao.prepareStatement(sqlLivros);
            stmtLivros.setString(1, nomeAutor + "%"); // busca por prefixo
            ResultSet rsLivros = stmtLivros.executeQuery();

            while (rsLivros.next()) {
                int idLivro = rsLivros.getInt("id_livro");
                String titulo = rsLivros.getString("titulo");
                String isbn = rsLivros.getString("isbn");
                String nomeCompletoAutor = rsLivros.getString("autor");

                // Contar cópias disponíveis
                String sqlCopias = """
                SELECT COUNT(*) AS num_copias
                FROM copia_livro
                WHERE id_livro = ? AND status_livro = 'disponivel'
            """;

                PreparedStatement stmtCopias = conexao.prepareStatement(sqlCopias);
                stmtCopias.setInt(1, idLivro);
                ResultSet rsCopias = stmtCopias.executeQuery();

                int numCopiasDisponiveis = 0;
                if (rsCopias.next()) {
                    numCopiasDisponiveis = rsCopias.getInt("num_copias");
                }
                stmtCopias.close();

                livros.add(new Livro(titulo, isbn, numCopiasDisponiveis, nomeCompletoAutor));
            }

            stmtLivros.close();

        } catch (SQLException e) {
            System.out.println("Erro ao buscar livros do autor: " + e.getMessage());
        }

        return livros.isEmpty() ? Optional.empty() : Optional.of(livros);
    }


    public Optional<List<Emprestimo>> buscarEmprestimosPorEmail(String email) {
        List<Emprestimo> emprestimos = new ArrayList<>();

        try {
            // Buscar ID do membro pelo email
            String sqlMembro = "SELECT id_membro FROM membros WHERE email = ?";
            PreparedStatement stmtMembro = conexao.prepareStatement(sqlMembro);
            stmtMembro.setString(1, email);
            ResultSet rsMembro = stmtMembro.executeQuery();

            int idMembro;
            if (rsMembro.next()) {
                idMembro = rsMembro.getInt("id_membro");
            } else {
                System.out.println("Membro não encontrado!");
                return Optional.of(emprestimos);
            }
            stmtMembro.close();

            // Buscar empréstimos vinculados ao membro
            String sqlEmprestimos = "SELECT emprestimo.*, livro.titulo FROM emprestimo\n JOIN copia_livro ON emprestimo.id_copia_livro = copia_livro.id_copia_livro JOIN livro ON copia_livro.id_livro = livro.id_livro WHERE emprestimo.id_membro = ?;";
            PreparedStatement stmtEmprestimos = conexao.prepareStatement(sqlEmprestimos);
            stmtEmprestimos.setInt(1, idMembro);
            ResultSet rsEmprestimos = stmtEmprestimos.executeQuery();

            while (rsEmprestimos.next()) {
                String titulo = rsEmprestimos.getString("titulo");
                Timestamp dataEmprestimo = rsEmprestimos.getTimestamp("data_emprestimo");
                Timestamp dataDevolucao = rsEmprestimos.getTimestamp("data_devolucao");
                String statusEmprestimo = rsEmprestimos.getString("status_emprestimo");
                double multa = rsEmprestimos.getDouble("multa");

                emprestimos.add(EmprestimoFactory.criarEmprestimo(titulo, email, dataEmprestimo, dataDevolucao, statusEmprestimo, multa));
            }
            stmtEmprestimos.close();

        } catch (SQLException e) {
            System.out.println("Erro ao buscar empréstimos: " + e.getMessage());
        }

        return Optional.of(emprestimos);
    }


    public Optional<Membro> buscarMembroPorEmail(String email) {
        try {
            String sql = "SELECT nome, email, senha_hash, telefone, papel FROM membros WHERE email = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String senhaHash = rs.getString("senha_hash");
                String telefone = rs.getString("telefone");
                String papel = rs.getString("papel");
                PapelMembro papelmembro = PapelMembro.CLIENTE;

                if(papel.equals("bibliotecario")) {
                    papelmembro = PapelMembro.BIBLIOTECARIO;
                }

                stmt.close();
                return Optional.of(new Membro(nome, email, senhaHash, telefone, papelmembro));
            } else {
                stmt.close();
                System.out.println("Membro não encontrado!");
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar membro: " + e.getMessage());
            return Optional.empty();
        }
    }


    public boolean inserirLivro(String titulo, String isbn, String nomeAutor) {
        try {

            // Verificar se o autor já existe
            String sqlAutor = "SELECT id_autor FROM autor WHERE nome = ?";
            PreparedStatement stmtAutor = conexao.prepareStatement(sqlAutor);
            stmtAutor.setString(1, nomeAutor);
            ResultSet rsAutor = stmtAutor.executeQuery();

            int idAutor;
            if (rsAutor.next()) {
                idAutor = rsAutor.getInt("id_autor"); // Autor já existe
            } else {
                // Inserir novo autor
                String sqlInserirAutor = "INSERT INTO autor (nome) VALUES (?)";
                PreparedStatement stmtInserirAutor = conexao.prepareStatement(sqlInserirAutor, PreparedStatement.RETURN_GENERATED_KEYS);
                stmtInserirAutor.setString(1, nomeAutor);
                stmtInserirAutor.executeUpdate();

                ResultSet rsNovoAutor = stmtInserirAutor.getGeneratedKeys();
                if (rsNovoAutor.next()) {
                    idAutor = rsNovoAutor.getInt(1); // ID do novo autor
                } else {
                    throw new SQLException("Erro ao inserir autor.");
                }
                stmtInserirAutor.close();
            }
            stmtAutor.close();

            // Verificar se o livro já existe
            String sqlLivroExistente = "SELECT id_livro FROM livro WHERE titulo = ? AND id_autor = ?";
            PreparedStatement stmtLivroExistente = conexao.prepareStatement(sqlLivroExistente);
            stmtLivroExistente.setString(1, titulo);
            stmtLivroExistente.setInt(2, idAutor);
            ResultSet rsLivroExistente = stmtLivroExistente.executeQuery();

            int idLivro;
            if (rsLivroExistente.next()) {
                idLivro = rsLivroExistente.getInt("id_livro"); // Livro já existe
                System.out.println("Livro já cadastrado. Criando nova cópia...");
                stmtLivroExistente.close();

            } else {
                String sqlInserirLivro = "INSERT INTO livro (titulo, isbn, id_autor) VALUES (?, ?, ?)";
                PreparedStatement stmtInserirLivro = conexao.prepareStatement(sqlInserirLivro);
                stmtInserirLivro.setString(1, titulo);
                stmtInserirLivro.setString(2, isbn);
                stmtInserirLivro.setInt(3, idAutor);
                stmtInserirLivro.executeUpdate();

                String sqlGetIDLivro = "SELECT id_livro FROM livro WHERE titulo = ? AND isbn = ?";
                PreparedStatement stmtGetIDLivro = conexao.prepareStatement(sqlGetIDLivro);
                stmtGetIDLivro.setString(1, titulo);
                stmtGetIDLivro.setString(2, isbn);
                ResultSet rsIDLivro = stmtGetIDLivro.executeQuery();
                if (rsIDLivro.next()) {
                    idLivro = rsIDLivro.getInt("id_livro");
                } else {
                    throw new SQLException("Erro ao recuperar id do livro.");
                }
                stmtGetIDLivro.close();

                System.out.println("Livro inserido com sucesso!");
            }

            // Criar uma nova cópia do livro
            String sqlInserirCopia = "INSERT INTO copia_livro (id_livro) VALUES (?)";
            PreparedStatement stmtInserirCopia = conexao.prepareStatement(sqlInserirCopia);
            stmtInserirCopia.setInt(1, idLivro);
            stmtInserirCopia.executeUpdate();
            stmtInserirCopia.close();
            System.out.println("Nova cópia criada com sucesso!");

            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir livro ou criar cópia: " + e.getMessage());
        }
        return false;
    }

    public boolean inserirMembro(String nome, String email, String telefone, PapelMembro papel, String senhaHash) {
        try {
            String sqlFirstVerification = "SELECT * FROM membros";
            PreparedStatement FirstVerification = conexao.prepareStatement(sqlFirstVerification);
            ResultSet rsFirstVerification = FirstVerification.executeQuery();
            if(rsFirstVerification.next()) {
                // Verificar se o membro já existe pelo email ou telefone
                String sqlVerificar = "SELECT id_membro FROM membros WHERE email = ? OR telefone = ?";
                PreparedStatement stmtVerificar = conexao.prepareStatement(sqlVerificar);
                stmtVerificar.setString(1, email);
                stmtVerificar.setString(2, telefone);
                ResultSet rsVerificar = stmtVerificar.executeQuery();

                if (rsVerificar.next()) {
                    System.out.println("Membro já cadastrado! Não será inserido novamente.");
                    return true; // Sai do metodo sem inserir um novo membro
                }
                stmtVerificar.close();
            }

            // Inserir novo membro
            senhaHash = BCrypt.hashpw(senhaHash, BCrypt.gensalt(12)); //hashing da senha;
            String sqlInserir = "INSERT INTO membros (nome, email, telefone, papel, senha_hash) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtInserir = conexao.prepareStatement(sqlInserir);
            stmtInserir.setString(1, nome);
            stmtInserir.setString(2, email);
            stmtInserir.setString(3, telefone);
            stmtInserir.setString(4, papel.name()); // Converte o enum para String
            stmtInserir.setString(5, senhaHash);

            stmtInserir.executeUpdate();
            stmtInserir.close();

            System.out.println("Membro inserido com sucesso!");
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir membro: " + e.getMessage());
        }
        return false;
    }

    public Optional<Bibliotecario> autenticarBibliotecario(String email, String senha) {
        Bibliotecario retorno = null;
        try {
            String sql = "SELECT * FROM membros WHERE email = ? AND papel = 'bibliotecario'";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashArmazenado = rs.getString("senha_hash");
                String nome = rs.getString("nome");
                String telefone = rs.getString("telefone");
                stmt.close();
                if(BCrypt.checkpw(senha, hashArmazenado)) {
                    System.out.println("\nRetornei o bibliotecario");
                    return Optional.of(new Bibliotecario(nome, telefone, email, senha));
                }

            } else {
                stmt.close();
                return Optional.empty(); // Bibliotecário não encontrado ou senha incorreta
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar bibliotecário: " + e.getMessage());
            return Optional.empty();
        }
        return Optional.empty();
    }


    public boolean validarLogin(String email, String senha) {
        try {
            // Buscar o hash da senha no banco pelo email
            String sql = "SELECT senha_hash FROM membros WHERE email = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashArmazenado = rs.getString("senha_hash");
                stmt.close();

                // Comparar a senha fornecida com o hash armazenado
                return BCrypt.checkpw(senha, hashArmazenado);
            } else {
                stmt.close();
                return false; // Email não encontrado
            }
        } catch (SQLException e) {
            System.out.println("Erro ao validar login: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarDadosUsuario( String emailUsuario,String senha_usuario, String novoEmail, String novaSenha, String novoTelefone) {
        try {


            if(!validarLogin(emailUsuario, senha_usuario)) {
                System.out.println("Email e senha incorretos");
                return false;
            }

            // Verificar se o usuário existe
            String sqlVerificarUsuario = "SELECT id_membro FROM membros WHERE email = ?";
            PreparedStatement stmtVerificarUsuario = conexao.prepareStatement(sqlVerificarUsuario);
            stmtVerificarUsuario.setString(1, emailUsuario);
            ResultSet rsUsuario = stmtVerificarUsuario.executeQuery();

            int idUsuario;
            if (rsUsuario.next()) {
                idUsuario = rsUsuario.getInt("id_membro");
            } else {
                System.out.println("Usuário não encontrado!");
                return false;
            }
            stmtVerificarUsuario.close();

            // Atualizar os dados do usuário
            String sqlAtualizar = "UPDATE membros SET email = ?, senha_hash = ?, telefone = ? WHERE id_membro = ?";
            PreparedStatement stmtAtualizar = conexao.prepareStatement(sqlAtualizar);
            stmtAtualizar.setString(1, novoEmail);
            stmtAtualizar.setString(2, BCrypt.hashpw(novaSenha, BCrypt.gensalt(12))); // Gerar novo hash da senha
            stmtAtualizar.setString(3, novoTelefone);
            stmtAtualizar.setInt(4, idUsuario);

            int linhasAfetadas = stmtAtualizar.executeUpdate();
            stmtAtualizar.close();

            if (linhasAfetadas > 0) {
                System.out.println("Dados do usuário atualizados com sucesso!");
                return true;
            } else {
                System.out.println("Nenhuma alteração foi feita.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar dados do usuário: " + e.getMessage());
            return false;
        }
    }


    public boolean registrarEmprestimo(String emailMembro, String tituloLivro) {
        try {

            // Verificar se o membro existe
            String sqlVerificarMembro = "SELECT id_membro FROM membros WHERE email = ?";
            PreparedStatement stmtVerificarMembro = conexao.prepareStatement(sqlVerificarMembro);
            stmtVerificarMembro.setString(1, emailMembro);
            ResultSet rsMembro = stmtVerificarMembro.executeQuery();

            int idMembro;
            if (rsMembro.next()) {
                idMembro = rsMembro.getInt("id_membro");
            } else {
                System.out.println("Membro não encontrado!");
                return false;
            }
            stmtVerificarMembro.close();

            // Verificar se há cópias disponíveis
            String sqlCopiaDisponivel = "SELECT id_copia_livro FROM copia_livro WHERE id_livro = (SELECT id_livro FROM livro WHERE titulo = ?) AND status_livro = 'disponivel' LIMIT 1";
            PreparedStatement stmtCopiaDisponivel = conexao.prepareStatement(sqlCopiaDisponivel);
            stmtCopiaDisponivel.setString(1, tituloLivro);
            ResultSet rsCopia = stmtCopiaDisponivel.executeQuery();

            int idCopia;
            if (rsCopia.next()) {
                idCopia = rsCopia.getInt("id_copia_livro");
            } else {
                System.out.println("Nenhuma cópia disponível para empréstimo!");
                return false;
            }
            stmtCopiaDisponivel.close();

            // Registrar o empréstimo com data atual e devolução em 7 dias
            String sqlRegistrarEmprestimo = "INSERT INTO emprestimo (id_copia_livro, id_membro, data_emprestimo, data_devolucao, status_emprestimo, multa) VALUES (?, ?, NOW(), NOW() + INTERVAL 7 DAY, 'ativo', 0)";
            PreparedStatement stmtRegistrarEmprestimo = conexao.prepareStatement(sqlRegistrarEmprestimo);
            stmtRegistrarEmprestimo.setInt(1, idCopia);
            stmtRegistrarEmprestimo.setInt(2, idMembro);
            stmtRegistrarEmprestimo.executeUpdate();
            stmtRegistrarEmprestimo.close();

            // Atualizar status da cópia para 'emprestado'
            String sqlAtualizarCopia = "UPDATE copia_livro SET status_livro = 'emprestado' WHERE id_copia_livro = ?";
            PreparedStatement stmtAtualizarCopia = conexao.prepareStatement(sqlAtualizarCopia);
            stmtAtualizarCopia.setInt(1, idCopia);
            stmtAtualizarCopia.executeUpdate();
            stmtAtualizarCopia.close();

            System.out.println("Empréstimo registrado com sucesso!");
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao registrar empréstimo: " + e.getMessage());
            return false;
        }
    }


    public boolean darBaixaEmprestimo(String emailMembro, String tituloLivro) {
        try {
            // 1. Buscar o ID do membro pelo email
            String sqlMembro = "SELECT id_membro FROM membros WHERE email = ?";
            PreparedStatement stmtMembro = conexao.prepareStatement(sqlMembro);
            stmtMembro.setString(1, emailMembro);
            ResultSet rsMembro = stmtMembro.executeQuery();

            if (!rsMembro.next()) {
                System.out.println("Membro não encontrado!");
                return false;
            }

            int idMembro = rsMembro.getInt("id_membro");
            stmtMembro.close();

            // 2. Buscar o empréstimo ativo ou atrasado do livro desejado
            String sqlEmprestimo = "SELECT id_emprestimo, id_copia_livro, DATEDIFF(NOW(), data_devolucao) AS dias_atraso " +
                    "FROM emprestimo WHERE id_membro = ? AND id_copia_livro = (" +
                    "SELECT id_copia_livro FROM copia_livro WHERE id_livro = " +
                    "(SELECT id_livro FROM livro WHERE titulo = ?) LIMIT 1) " +
                    "AND (status_emprestimo = 'ativo' OR status_emprestimo = 'atrasado')";

            PreparedStatement stmtEmprestimo = conexao.prepareStatement(sqlEmprestimo);
            stmtEmprestimo.setInt(1, idMembro);
            stmtEmprestimo.setString(2, tituloLivro);
            ResultSet rsEmprestimo = stmtEmprestimo.executeQuery();

            if (!rsEmprestimo.next()) {
                System.out.println("Nenhum empréstimo ativo ou atrasado encontrado.");
                return false;
            }

            int idEmprestimo = rsEmprestimo.getInt("id_emprestimo");
            int idCopiaLivro = rsEmprestimo.getInt("id_copia_livro");
            int diasAtraso = rsEmprestimo.getInt("dias_atraso");
            stmtEmprestimo.close();

            // 3. Decidir multa e status com base no atraso
            double multa = 0;
            String novoStatus = "concluído";

            if (diasAtraso > 0) {
                if (diasAtraso > 10) {
                    CalculadoraMulta calculadora = new CalculadoraMulta(new MultaBasica());
                    multa = calculadora.calcular(diasAtraso);
                }
                if (multa > 0) {
                    novoStatus = "atrasado";
                }
            }

            // 4. Atualizar o empréstimo (multa, status e devolução)
            String sqlUpdateEmprestimo = "UPDATE emprestimo SET multa = ?, status_emprestimo = ?, data_devolucao = NOW() " +
                    "WHERE id_emprestimo = ?";
            PreparedStatement stmtUpdateEmprestimo = conexao.prepareStatement(sqlUpdateEmprestimo);
            stmtUpdateEmprestimo.setDouble(1, multa);
            stmtUpdateEmprestimo.setString(2, novoStatus);
            stmtUpdateEmprestimo.setInt(3, idEmprestimo);
            stmtUpdateEmprestimo.executeUpdate();
            stmtUpdateEmprestimo.close();

            // 5. Atualizar a cópia para disponível
            String sqlAtualizarCopia = "UPDATE copia_livro SET status_livro = 'disponivel' WHERE id_copia_livro = ?";
            PreparedStatement stmtAtualizarCopia = conexao.prepareStatement(sqlAtualizarCopia);
            stmtAtualizarCopia.setInt(1, idCopiaLivro);
            stmtAtualizarCopia.executeUpdate();
            stmtAtualizarCopia.close();

            // 6. Mensagem final
            System.out.printf("Empréstimo finalizado com status '%s'. Multa aplicada: R$ %.2f\n", novoStatus, multa);
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao dar baixa no empréstimo: " + e.getMessage());
            return false;
        }
    }

    public boolean excluirCopiasLivro( String tituloLivro, String isbn, int quantidade) {
        try {


            // Verificar se o livro existe
            String sqlLivro = "SELECT id_livro, id_autor FROM livro WHERE titulo = ? AND isbn = ?";
            PreparedStatement stmtLivro = conexao.prepareStatement(sqlLivro);
            stmtLivro.setString(1, tituloLivro);
            stmtLivro.setString(2, isbn);
            ResultSet rsLivro = stmtLivro.executeQuery();

            int idLivro, idAutor;
            if (rsLivro.next()) {
                idLivro = rsLivro.getInt("id_livro");
                idAutor = rsLivro.getInt("id_autor");
            } else {
                System.out.println("Livro não encontrado!");
                return false;
            }
            stmtLivro.close();

            // Contar quantas cópias existem
            String sqlContarCopias = "SELECT COUNT(*) AS total_copias FROM copia_livro WHERE id_livro = ?";
            PreparedStatement stmtContarCopias = conexao.prepareStatement(sqlContarCopias);
            stmtContarCopias.setInt(1, idLivro);
            ResultSet rsCopias = stmtContarCopias.executeQuery();

            int totalCopias = 0;
            if (rsCopias.next()) {
                totalCopias = rsCopias.getInt("total_copias");
            }
            stmtContarCopias.close();

            // Buscar cópias disponíveis para exclusão
            String sqlCopiasDisponiveis = "SELECT id_copia_livro FROM copia_livro WHERE id_livro = ? AND id_copia_livro NOT IN (SELECT id_copia_livro FROM emprestimo) LIMIT ?";
            PreparedStatement stmtCopiasDisponiveis = conexao.prepareStatement(sqlCopiasDisponiveis);
            stmtCopiasDisponiveis.setInt(1, idLivro);
            stmtCopiasDisponiveis.setInt(2, quantidade);
            ResultSet rsCopiasDisponiveis = stmtCopiasDisponiveis.executeQuery();

            int copiasExcluidas = 0;
            while (rsCopiasDisponiveis.next()) {
                int idCopia = rsCopiasDisponiveis.getInt("id_copia_livro");

                // Excluir cópia disponível
                String sqlExcluirCopia = "DELETE FROM copia_livro WHERE id_copia_livro = ?";
                PreparedStatement stmtExcluirCopia = conexao.prepareStatement(sqlExcluirCopia);
                stmtExcluirCopia.setInt(1, idCopia);
                stmtExcluirCopia.executeUpdate();
                stmtExcluirCopia.close();

                copiasExcluidas++;
            }
            stmtCopiasDisponiveis.close();

            // Se ainda houver cópias a excluir, mas todas estão atreladas a empréstimos, excluir empréstimos antes das cópias
            if (copiasExcluidas < quantidade) {
                String sqlExcluirEmprestimos = "DELETE FROM emprestimo WHERE id_copia_livro IN (SELECT id_copia_livro FROM copia_livro WHERE id_livro = ?)";
                PreparedStatement stmtExcluirEmprestimos = conexao.prepareStatement(sqlExcluirEmprestimos);
                stmtExcluirEmprestimos.setInt(1, idLivro);
                stmtExcluirEmprestimos.executeUpdate();
                stmtExcluirEmprestimos.close();

                // Excluir as cópias restantes
                String sqlExcluirCopiasRestantes = "DELETE FROM copia_livro WHERE id_livro = ? LIMIT ?";
                PreparedStatement stmtExcluirCopiasRestantes = conexao.prepareStatement(sqlExcluirCopiasRestantes);
                stmtExcluirCopiasRestantes.setInt(1, idLivro);
                stmtExcluirCopiasRestantes.setInt(2, quantidade - copiasExcluidas);
                stmtExcluirCopiasRestantes.executeUpdate();
                stmtExcluirCopiasRestantes.close();
            }

            // Se todas as cópias foram excluídas, remover o livro e o autor (se necessário)
            if (quantidade >= totalCopias) {
                String sqlExcluirLivro = "DELETE FROM livro WHERE id_livro = ?";
                PreparedStatement stmtExcluirLivro = conexao.prepareStatement(sqlExcluirLivro);
                stmtExcluirLivro.setInt(1, idLivro);
                stmtExcluirLivro.executeUpdate();
                stmtExcluirLivro.close();

                // Verificar se o autor tem outros livros
                String sqlVerificarAutor = "SELECT COUNT(*) AS total FROM livro WHERE id_autor = ?";
                PreparedStatement stmtVerificarAutor = conexao.prepareStatement(sqlVerificarAutor);
                stmtVerificarAutor.setInt(1, idAutor);
                ResultSet rsAutor = stmtVerificarAutor.executeQuery();

                if (rsAutor.next() && rsAutor.getInt("total") == 0) {
                    // Excluir o autor se ele não tiver mais livros
                    String sqlExcluirAutor = "DELETE FROM autor WHERE id_autor = ?";
                    PreparedStatement stmtExcluirAutor = conexao.prepareStatement(sqlExcluirAutor);
                    stmtExcluirAutor.setInt(1, idAutor);
                    stmtExcluirAutor.executeUpdate();
                    stmtExcluirAutor.close();
                    System.out.println("Autor excluído, pois não possui mais livros cadastrados.");
                }
                stmtVerificarAutor.close();

                System.out.println("Todas as cópias do livro foram excluídas, e o livro foi removido!");
            }

            System.out.println("Copias Excluidas com sucesso!");
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir cópias do livro: " + e.getMessage());
            return false;
        }
    }



    public boolean excluirMembro( String email) {
        try {

            // Verificar se o usuário existe
            String sqlVerificarUsuario = "SELECT id_membro FROM membros WHERE email = ?";
            PreparedStatement stmtVerificarUsuario = conexao.prepareStatement(sqlVerificarUsuario);
            stmtVerificarUsuario.setString(1, email);
            ResultSet rsUsuario = stmtVerificarUsuario.executeQuery();

            int idUsuario;
            if (rsUsuario.next()) {
                idUsuario = rsUsuario.getInt("id_membro");
            } else {
                System.out.println("Usuário não encontrado!");
                return false;
            }
            stmtVerificarUsuario.close();

            // Atualizar status das cópias para 'disponível'
            String sqlAtualizarCopias = "UPDATE copia_livro SET status_livro = 'disponivel' WHERE id_copia_livro IN (SELECT id_copia_livro FROM emprestimo WHERE id_membro = ?)";
            PreparedStatement stmtAtualizarCopias = conexao.prepareStatement(sqlAtualizarCopias);
            stmtAtualizarCopias.setInt(1, idUsuario);
            stmtAtualizarCopias.executeUpdate();
            stmtAtualizarCopias.close();

            // Excluir os empréstimos do membro
            String sqlExcluirEmprestimos = "DELETE FROM emprestimo WHERE id_membro = ?";
            PreparedStatement stmtExcluirEmprestimos = conexao.prepareStatement(sqlExcluirEmprestimos);
            stmtExcluirEmprestimos.setInt(1, idUsuario);
            stmtExcluirEmprestimos.executeUpdate();
            stmtExcluirEmprestimos.close();


            //Deletar membro
            String sqlExclueUsuario = "DELETE FROM membros WHERE email = ?";
            PreparedStatement stmtExclueUsuario = conexao.prepareStatement(sqlExclueUsuario);
            stmtExclueUsuario.setString(1, email);
            stmtExclueUsuario.executeUpdate();
            stmtExclueUsuario.close();

            System.out.println("Membro excluido com sucesso");

            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir membro: " + e.getMessage());
            return false;
        }
    }
}
