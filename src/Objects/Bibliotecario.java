package Objects;

import main.database.DatabaseAPI;
import main.database.PapelMembro;

import java.sql.Connection;

public class Bibliotecario extends Membro{

    public Bibliotecario(String nome, String telefone, String email, String senha) {
        super(nome, telefone, email, senha);
    }

    public boolean registrarEmprestimo(String titulo, Membro membro, DatabaseAPI db) {
        var result =  db.registrarEmprestimo(this.getEmail(), this.getSenha(),membro.getEmail(), titulo);
        if (result) {
            System.out.println("Emprestimo realizado com sucesso");
        } else {
            System.out.println("Erro ao registrar emprestimo");
        }
        return result;
    }

    public boolean registrarDevolucao(String titulo, Membro membro, DatabaseAPI db) {
        var result = db.darBaixaEmprestimo(this.getEmail(), this.getSenha(), membro.getEmail(), titulo);
        if (result) {
            System.out.println("Devolucao realizada com sucesso");
        } else {
            System.out.println("Erro ao registrar devolucao");
        }
        return result;
    }

    public boolean alterarRegistroMembro(Membro membro, DatabaseAPI db, String novoEmail, String novaSenha, String novoTelefone) {
        var result = db.atualizarDadosUsuario(this.getEmail(), this.getSenha(), membro.getEmail(), membro.getSenha(), novoEmail, novaSenha, novoTelefone);
        if(result) {
            System.out.println("Registro alterado com sucesso");
        } else {
            System.out.println("Erro ao alterar registro");
        }
        return result;
    }

    public boolean registrarMembro(DatabaseAPI db, String nome, String email, String telefone, PapelMembro papel, String senha) {
        var result = db.inserirMembro(this.getEmail(), this.getSenha(), nome, email, telefone, papel, senha);
        if(result) {
            System.out.println("Membro inserido com sucesso");
        } else {
            System.out.println("Erro ao inserir membro");
        }
        return result;
    }

    public boolean registrarLivro(DatabaseAPI db, String titulo, String autor, String isbn) {
        var result = db.inserirLivro(this.getEmail(), this.getSenha(), titulo, autor, isbn);
        if(result) {
            System.out.println("Livro inserido com sucesso");
        } else {
            System.out.println("Erro ao inserir livro");
        }
        return result;
    }

    public boolean excluirMembro(DatabaseAPI db, String email) {
        var result = db.excluirMembro(this.getEmail(), this.getSenha(), email);
        if(result) {
            System.out.println("Membro excluido com sucesso");
        } else {
            System.out.println("Erro ao excluir membro");
        }
        return result;
    }

    public boolean excluirCopiaLivro(DatabaseAPI db, String titulo, int quantidade) {
        var result = db.excluirCopiasLivro(this.getEmail(), this.getSenha(), titulo, quantidade);
        if(result) {
            System.out.println("Copia excluida com sucesso");
        } else {
            System.out.println("Erro ao excluir copia");
        }
        return result;
    }
}
