package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import main.database.PapelMembro;
import main.database.DatabaseAPI;

import java.io.IOException;

public class RegistrarMembroController {

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private TextField campoTelefone;
    @FXML private PasswordField campoSenha;
    @FXML private ComboBox<String> comboPapel;
    private DatabaseAPI db = new DatabaseAPI();

    @FXML
    private void handleRegistrar() {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String senha = campoSenha.getText();
        String papelStr = comboPapel.getValue();

        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || senha.isEmpty() || papelStr == null) {
            mostrarAlerta("Preencha todos os campos.");
            return;
        }

        PapelMembro papel = PapelMembro.valueOf(papelStr);

        boolean sucesso = db.inserirMembro(nome, email, telefone, papel, senha);

        if (sucesso) {
            mostrarAlerta("Membro registrado com sucesso!");
            limparCampos();
        } else {
            mostrarAlerta("Erro ao registrar membro.");
        }
    }

    @FXML
    private void handleVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/MainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) campoNome.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu Principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao voltar ao menu.");
        }
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparCampos() {
        campoNome.clear();
        campoEmail.clear();
        campoTelefone.clear();
        campoSenha.clear();
        comboPapel.getSelectionModel().clearSelection();
    }
}