package main.database.tests;

import main.database.DatabaseAPI;
import main.database.Emprestimo;
import main.database.Livro;
import main.database.PapelMembro;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseAPITest {

    static DatabaseAPI db;
    static final String TITULO_TESTE = "Livro de Teste";
    static final String ISBN_TESTE = "1234567890";
    static final String AUTOR_TESTE = "Autor Teste";
    static final String EMAIL_TESTE = "teste@exemplo.com";
    static final String TELEFONE_TESTE = "11999999999";

    @BeforeAll
    static void setup() {
        db = new DatabaseAPI();
        db.inserirMembro("Usuário Teste", EMAIL_TESTE, TELEFONE_TESTE, PapelMembro.CLIENTE, "senha123");
    }

    @Test
    @Order(1)
    void testeInserirLivro() {
        assertTrue(db.inserirLivro(TITULO_TESTE, ISBN_TESTE, AUTOR_TESTE));
    }

    @Test
    @Order(2)
    void testeBuscarLivroPorTitulo() {
        Optional<List<Livro>> resultado = db.buscarLivrosPorTituloInicial("Livro de");
        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().stream().anyMatch(livro -> livro.getTitulo().equals(TITULO_TESTE)));
    }

    @Test
    @Order(3)
    void testeBuscarLivroPorAutor() {
        Optional<List<Livro>> resultado = db.buscarLivrosPorAutor("Autor");
        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().stream().anyMatch(livro -> livro.getAutor().equals(AUTOR_TESTE)));
    }

    @Test
    @Order(4)
    void testeRegistrarEmprestimo() {
        assertTrue(db.registrarEmprestimo(EMAIL_TESTE, TITULO_TESTE));
    }

    @Test
    @Order(5)
    void testeBuscarEmprestimosPorEmail() {
        Optional<List<Emprestimo>> emprestimos = db.buscarEmprestimosPorEmail(EMAIL_TESTE);
        assertTrue(emprestimos.isPresent());
        assertFalse(emprestimos.get().isEmpty());
    }

    @Test
    @Order(6)
    void testeDarBaixaEmprestimo() {
        assertTrue(db.darBaixaEmprestimo(EMAIL_TESTE, TITULO_TESTE));
    }

    @Test
    @Order(7)
    void testeAtualizarUsuario() {
        String novoEmail = "novo_" + EMAIL_TESTE;
        String novoTelefone = "11888888888";
        String novaSenha = "novaSenha123";

        assertTrue(db.atualizarDadosUsuario(EMAIL_TESTE, "senha123", novoEmail, novaSenha, novoTelefone));

        // Reverte e valida
        assertTrue(db.atualizarDadosUsuario(novoEmail, novaSenha, EMAIL_TESTE, "senha123", TELEFONE_TESTE));
    }

    @Test
    @Order(8)
    void testeExcluirCopiaELivro() {
        Optional<List<Livro>> livros = db.buscarLivrosPorTituloInicial(TITULO_TESTE);
        assertTrue(livros.isPresent());
        Livro livro = livros.get().get(0);
        assertTrue(db.excluirCopiasLivro(TITULO_TESTE, livro.getIsbn(), livro.getNum_copias()));
    }

    @Test
    @Order(9)
    void testeExcluirMembro() {
        assertTrue(db.excluirMembro(EMAIL_TESTE));
    }
}