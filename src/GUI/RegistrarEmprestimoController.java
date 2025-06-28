package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import main.database.DatabaseAPI;

import java.io.IOException;

public class RegistrarEmprestimoController {

    @FXML private TextField campoEmail;
    @FXML private TextField campoTitulo;
    @FXML private Label mensagemLabel;

    private final DatabaseAPI db = new DatabaseAPI();

    @FXML
    private void handleRegistrar() {
        String email = campoEmail.getText().trim();
        String titulo = campoTitulo.getText().trim();

        if (email.isEmpty() || titulo.isEmpty()) {
            mensagemLabel.setText("Preencha todos os campos.");
            return;
        }

        boolean sucesso = db.registrarEmprestimo(email, titulo);

        if (sucesso) {
            mensagemLabel.setText("Empréstimo registrado com sucesso!");
            limparCampos();
        } else {
            mensagemLabel.setText("Erro ao registrar empréstimo. Verifique se o membro e o livro existem e se há cópias disponíveis.");
        }
    }

    @FXML
    private void handleVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/MainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) campoEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu Principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao voltar ao menu.");
        }
    }

    private void limparCampos() {
        campoEmail.clear();
        campoTitulo.clear();
    }
}