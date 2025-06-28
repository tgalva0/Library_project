package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import main.database.DatabaseAPI;

import java.io.IOException;

public class RegistrarLivroController {

    @FXML private TextField campoTitulo;
    @FXML private TextField campoISBN;
    @FXML private TextField campoAutor;
    @FXML private Label mensagemLabel;
    private DatabaseAPI db = new DatabaseAPI();

    @FXML
    private void handleRegistrarLivro() {
        String titulo = campoTitulo.getText().trim();
        String isbn = campoISBN.getText().trim();
        String autor = campoAutor.getText().trim();

        if (titulo.isEmpty() || isbn.isEmpty() || autor.isEmpty()) {
            mensagemLabel.setText("Por favor, preencha todos os campos.");
            return;
        }

        boolean sucesso = db.inserirLivro(titulo, isbn, autor);

        if (sucesso) {
            mensagemLabel.setText("Livro registrado ou cópia adicionada com sucesso.");
            limparCampos();
        } else {
            mensagemLabel.setText("Erro ao registrar o livro.");
        }
    }

    @FXML
    private void handleVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/MainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) campoTitulo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu Principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao voltar ao menu.");
        }
    }

    private void limparCampos() {
        campoTitulo.clear();
        campoISBN.clear();
        campoAutor.clear();
    }
}