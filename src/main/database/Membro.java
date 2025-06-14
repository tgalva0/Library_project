package main.database;

public class Membro {
    private final String nome;
    private String email;
    private String senha_hash;
    private String telefone;
    private PapelMembro papel;

    public Membro(String nome, String email, String senha, String telefone, PapelMembro papel) {
        this.nome = nome;
        this.email = email;
        this.senha_hash = senha;
        this.telefone = telefone;
        this.papel = papel;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha_hash;
    }

    public String getTelefone() {
        return telefone;
    }

    public PapelMembro getPapel() {
        return papel;
    }

    public String toString() {
        return ("\nNome = " + nome + ",\nemail = " + email);
    }
}
