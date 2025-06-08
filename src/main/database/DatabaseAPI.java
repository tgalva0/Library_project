package main.database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAPI {
    Connection conexao = MyJDBC.getConnection();

    public boolean inserirLivro(String emailBibliotecario, String senhaBibliotecario, String titulo, String isbn, String nomeAutor) {
        try {
            // Autenticar bibliotecário
            if (!autenticarBibliotecario(emailBibliotecario, senhaBibliotecario)) {
                System.out.println("Apenas bibliotecários podem excluir cópias de livros!");
                return false;
            }

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
            String sqlLivroExistente = "SELECT id_livro FROM livro WHERE titulo = ? AND isbn = ?";
            PreparedStatement stmtLivroExistente = conexao.prepareStatement(sqlLivroExistente);
            stmtLivroExistente.setString(1, titulo);
            stmtLivroExistente.setString(2, isbn);
            ResultSet rsLivroExistente = stmtLivroExistente.executeQuery();

            int idLivro;
            if (rsLivroExistente.next()) {
                idLivro = rsLivroExistente.getInt("id_livro"); // Livro já existe
                System.out.println("Livro já cadastrado. Criando nova cópia...");

                // Criar uma nova cópia do livro
                String sqlInserirCopia = "INSERT INTO copia_livro (id_livro) VALUES (?)";
                PreparedStatement stmtInserirCopia = conexao.prepareStatement(sqlInserirCopia);
                stmtInserirCopia.setInt(1, idLivro);
                stmtInserirCopia.executeUpdate();
                stmtInserirCopia.close();

                System.out.println("Nova cópia criada com sucesso!");
            } else {
                // Inserir novo livro (o trigger cuidará da cópia automaticamente)
                String sqlInserirLivro = "INSERT INTO livro (titulo, isbn, id_autor) VALUES (?, ?, ?)";
                PreparedStatement stmtInserirLivro = conexao.prepareStatement(sqlInserirLivro);
                stmtInserirLivro.setString(1, titulo);
                stmtInserirLivro.setString(2, isbn);
                stmtInserirLivro.setInt(3, idAutor);
                stmtInserirLivro.executeUpdate();
                stmtInserirLivro.close();

                System.out.println("Livro inserido com sucesso!");
            }
            stmtLivroExistente.close();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir livro ou criar cópia: " + e.getMessage());
        }
        return false;
    }

    public boolean inserirMembro(String emailBibliotecario, String senhaBibliotecario, String nome, String email, String telefone, PapelMembro papel, String senhaHash) {
        try {
            // Autenticar bibliotecário
            if (!autenticarBibliotecario(emailBibliotecario, senhaBibliotecario)) {
                System.out.println("Apenas bibliotecários podem excluir cópias de livros!");
                return false;
            }
            // Verificar se o membro já existe pelo email ou telefone
            String sqlVerificar = "SELECT id_membro FROM membros WHERE email = ? OR telefone = ?";
            PreparedStatement stmtVerificar = conexao.prepareStatement(sqlVerificar);
            stmtVerificar.setString(1, email);
            stmtVerificar.setString(2, telefone);
            ResultSet rsVerificar = stmtVerificar.executeQuery();

            if (rsVerificar.next()) {
                System.out.println("Membro já cadastrado! Não será inserido novamente.");
                return true; // Sai do método sem inserir um novo membro
            }
            stmtVerificar.close();

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

    public boolean autenticarBibliotecario(String email, String senha) {
        try {
            String sql = "SELECT senha_hash FROM membros WHERE email = ? AND papel = 'bibliotecario'";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashArmazenado = rs.getString("senha_hash");
                stmt.close();
                return BCrypt.checkpw(senha, hashArmazenado);
            } else {
                stmt.close();
                return false; // Bibliotecário não encontrado ou senha incorreta
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar bibliotecário: " + e.getMessage());
            return false;
        }
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
//                return BCrypt.checkpw(senha, hashArmazenado);
                return senha.equals(hashArmazenado);
            } else {
                stmt.close();
                return false; // Email não encontrado
            }
        } catch (SQLException e) {
            System.out.println("Erro ao validar login: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarDadosUsuario(String emailBibliotecario, String senhaBibliotecario, String emailUsuario,String senha_usuario, String novoEmail, String novaSenha, String novoTelefone) {
        try {
            // Autenticar bibliotecário
            if (!autenticarBibliotecario(emailBibliotecario, senhaBibliotecario)) {
                System.out.println("Apenas bibliotecários podem alterar dados de usuários!");
                return false;
            }

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


    public boolean registrarEmprestimo(String emailBibliotecario, String senhaBibliotecario, String emailMembro, String tituloLivro) {
        try {
            // Autenticar bibliotecário
            if (!autenticarBibliotecario(emailBibliotecario, senhaBibliotecario)) {
                System.out.println("Apenas bibliotecários podem registrar empréstimos!");
                return false;
            }

            // Verificar se o membro existe
            String sqlMembro = "SELECT id_membro FROM membros WHERE email = ?";
            PreparedStatement stmtMembro = conexao.prepareStatement(sqlMembro);
            stmtMembro.setString(1, emailMembro);
            ResultSet rsMembro = stmtMembro.executeQuery();

            int idMembro;
            if (rsMembro.next()) {
                idMembro = rsMembro.getInt("id_membro");
            } else {
                System.out.println("Membro não encontrado!");
                return false;
            }
            stmtMembro.close();

            // Verificar se o livro existe e se há cópias disponíveis
            String sqlCopiaDisponivel = "SELECT id_copia_livro FROM copia_livro WHERE id_livro = (SELECT id_livro FROM livro WHERE titulo = ?) AND status_livro = 'disponivel' LIMIT 1";
            PreparedStatement stmtCopia = conexao.prepareStatement(sqlCopiaDisponivel);
            stmtCopia.setString(1, tituloLivro);
            ResultSet rsCopia = stmtCopia.executeQuery();

            int idCopiaLivro;
            if (rsCopia.next()) {
                idCopiaLivro = rsCopia.getInt("id_copia_livro");
            } else {
                System.out.println("Nenhuma cópia disponível para empréstimo!");
                return false;
            }
            stmtCopia.close();

            // Criar o empréstimo
            String sqlEmprestimo = "INSERT INTO emprestimo (id_copia_livro, id_membro, status_emprestimo, multa) VALUES (?, ?, 'ativo', 0)";
            PreparedStatement stmtEmprestimo = conexao.prepareStatement(sqlEmprestimo);
            stmtEmprestimo.setInt(1, idCopiaLivro);
            stmtEmprestimo.setInt(2, idMembro);
            stmtEmprestimo.executeUpdate();
            stmtEmprestimo.close();

            // Atualizar o status da cópia para 'emprestado'
            String sqlAtualizarCopia = "UPDATE copia_livro SET status_livro = 'emprestado' WHERE id_copia_livro = ?";
            PreparedStatement stmtAtualizarCopia = conexao.prepareStatement(sqlAtualizarCopia);
            stmtAtualizarCopia.setInt(1, idCopiaLivro);
            stmtAtualizarCopia.executeUpdate();
            stmtAtualizarCopia.close();

            System.out.println("Empréstimo registrado com sucesso!");
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao registrar empréstimo: " + e.getMessage());
            return false;
        }
    }

    public boolean darBaixaEmprestimo(String emailBibliotecario, String senhaBibliotecario, String emailMembro, String tituloLivro) {
        try {
            // Autenticar bibliotecário
            if (!autenticarBibliotecario(emailBibliotecario, senhaBibliotecario)) {
                System.out.println("Apenas bibliotecários podem dar baixa em empréstimos!");
                return false;
            }

            // Verificar se o membro existe
            String sqlMembro = "SELECT id_membro FROM membros WHERE email = ?";
            PreparedStatement stmtMembro = conexao.prepareStatement(sqlMembro);
            stmtMembro.setString(1, emailMembro);
            ResultSet rsMembro = stmtMembro.executeQuery();

            int idMembro;
            if (rsMembro.next()) {
                idMembro = rsMembro.getInt("id_membro");
            } else {
                System.out.println("Membro não encontrado!");
                return false;
            }
            stmtMembro.close();

            // Verificar se há um empréstimo ativo ou atrasado para o livro
            String sqlEmprestimo = "SELECT id_emprestimo, id_copia_livro, status_emprestimo, DATEDIFF(NOW(), data_devolucao) AS dias_atraso " +
                    "FROM emprestimo WHERE id_membro = ? AND id_copia_livro = " +
                    "(SELECT id_copia_livro FROM copia_livro WHERE id_livro = (SELECT id_livro FROM livro WHERE titulo = ?) LIMIT 1) " +
                    "AND (status_emprestimo = 'ativo' OR status_emprestimo = 'atrasado')";
            PreparedStatement stmtEmprestimo = conexao.prepareStatement(sqlEmprestimo);
            stmtEmprestimo.setInt(1, idMembro);
            stmtEmprestimo.setString(2, tituloLivro);
            ResultSet rsEmprestimo = stmtEmprestimo.executeQuery();

            if (rsEmprestimo.next()) {
                int idEmprestimo = rsEmprestimo.getInt("id_emprestimo");
                int idCopiaLivro = rsEmprestimo.getInt("id_copia_livro");
                String statusEmprestimo = rsEmprestimo.getString("status_emprestimo");
                int diasAtraso = rsEmprestimo.getInt("dias_atraso");

                stmtEmprestimo.close();

                // Se o empréstimo estiver atrasado, calcular multa
                if (statusEmprestimo.equals("atrasado") && diasAtraso > 10) {
                    double multa = diasAtraso * 0.5;

                    String sqlAtualizarMulta = "UPDATE emprestimo SET multa = ? WHERE id_emprestimo = ?";
                    PreparedStatement stmtMulta = conexao.prepareStatement(sqlAtualizarMulta);
                    stmtMulta.setDouble(1, multa);
                    stmtMulta.setInt(2, idEmprestimo);
                    stmtMulta.executeUpdate();
                    stmtMulta.close();
                    System.out.println("Multa aplicada: R$ " + multa);
                }

                // Atualizar status do empréstimo para 'concluído'
                String sqlAtualizarEmprestimo = "UPDATE emprestimo SET status_emprestimo = 'concluído' WHERE id_emprestimo = ?";
                PreparedStatement stmtAtualizarEmprestimo = conexao.prepareStatement(sqlAtualizarEmprestimo);
                stmtAtualizarEmprestimo.setInt(1, idEmprestimo);
                stmtAtualizarEmprestimo.executeUpdate();
                stmtAtualizarEmprestimo.close();

                // Atualizar status da cópia do livro para 'disponível'
                String sqlAtualizarCopia = "UPDATE copia_livro SET status_livro = 'disponivel' WHERE id_copia_livro = ?";
                PreparedStatement stmtAtualizarCopia = conexao.prepareStatement(sqlAtualizarCopia);
                stmtAtualizarCopia.setInt(1, idCopiaLivro);
                stmtAtualizarCopia.executeUpdate();
                stmtAtualizarCopia.close();

                System.out.println("Empréstimo concluído e cópia do livro disponível!");
                return true;
            } else {
                System.out.println("Nenhum empréstimo ativo ou atrasado encontrado para este livro.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao dar baixa no empréstimo: " + e.getMessage());
            return false;
        }
    }

    public boolean excluirCopiasLivro(String emailBibliotecario, String senhaBibliotecario, String tituloLivro, boolean excluirTodas) {
        try {
            // Autenticar bibliotecário
            if (!autenticarBibliotecario(emailBibliotecario, senhaBibliotecario)) {
                System.out.println("Apenas bibliotecários podem excluir cópias de livros!");
                return false;
            }

            // Verificar se o livro existe
            String sqlLivro = "SELECT id_livro, id_autor FROM livro WHERE titulo = ?";
            PreparedStatement stmtLivro = conexao.prepareStatement(sqlLivro);
            stmtLivro.setString(1, tituloLivro);
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

            if (excluirTodas) {
                // Excluir todos os empréstimos atrelados às cópias do livro
                String sqlExcluirEmprestimos = "DELETE FROM emprestimo WHERE id_copia_livro IN (SELECT id_copia_livro FROM copia_livro WHERE id_livro = ?)";
                PreparedStatement stmtExcluirEmprestimos = conexao.prepareStatement(sqlExcluirEmprestimos);
                stmtExcluirEmprestimos.setInt(1, idLivro);
                stmtExcluirEmprestimos.executeUpdate();
                stmtExcluirEmprestimos.close();

                // Excluir todas as cópias do livro
                String sqlExcluirCopias = "DELETE FROM copia_livro WHERE id_livro = ?";
                PreparedStatement stmtExcluirCopias = conexao.prepareStatement(sqlExcluirCopias);
                stmtExcluirCopias.setInt(1, idLivro);
                stmtExcluirCopias.executeUpdate();
                stmtExcluirCopias.close();

                // Excluir o próprio livro
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
            } else {
                // Excluir um único empréstimo atrelado à cópia antes de excluir a cópia
                String sqlExcluirEmprestimo = "DELETE FROM emprestimo WHERE id_copia_livro = (SELECT id_copia_livro FROM copia_livro WHERE id_livro = ? LIMIT 1)";
                PreparedStatement stmtExcluirEmprestimo = conexao.prepareStatement(sqlExcluirEmprestimo);
                stmtExcluirEmprestimo.setInt(1, idLivro);
                stmtExcluirEmprestimo.executeUpdate();
                stmtExcluirEmprestimo.close();

                // Excluir apenas uma cópia do livro
                String sqlExcluirUmaCopia = "DELETE FROM copia_livro WHERE id_livro = ? LIMIT 1";
                PreparedStatement stmtExcluirUmaCopia = conexao.prepareStatement(sqlExcluirUmaCopia);
                stmtExcluirUmaCopia.setInt(1, idLivro);
                int linhasAfetadas = stmtExcluirUmaCopia.executeUpdate();
                stmtExcluirUmaCopia.close();

                if (linhasAfetadas > 0) {
                    System.out.println("Uma cópia do livro foi excluída!");
                } else {
                    System.out.println("Nenhuma cópia disponível para exclusão.");
                    return false;
                }
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir cópias do livro: " + e.getMessage());
            return false;
        }
    }



}
