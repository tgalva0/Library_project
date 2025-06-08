package Objects;

import java.sql.Connection;

public class Membro {
    private final String nome;
    private String email;
    private String senha_hash;
    private String telefone;

    public Membro(String nome, String email, String senha, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha_hash = senha;
        this.telefone = telefone;
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
}
