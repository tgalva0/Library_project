package Objects;

import main.database.DatabaseAPI;
import main.database.PapelMembro;

import java.sql.Connection;

public class Bibliotecario extends Membro{

    public Bibliotecario(String nome, String telefone, String email, String senha) {
        super(nome, telefone, email, senha, PapelMembro.BIBLIOTECARIO);
    }
}
