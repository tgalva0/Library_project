package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import main.database.DatabaseAPI;

import java.io.IOException;

public class AtualizarMembroController {

    @FXML private Label labelEmailAtual;
    @FXML private Label labelTelefoneAtual;
    @FXML private TextField campoNovoEmail;
    @FXML private TextField campoNovoTelefone;
    @FXML private PasswordField campoSenhaAtual;
    @FXML private PasswordField campoNovaSenha;
    @FXML private Label mensagemLabel;

    private String emailOriginal;
    private DatabaseAPI db = new DatabaseAPI();

    public void setDadosAtuais(String email, String telefone) {
        this.emailOriginal = email;
        labelEmailAtual.setText(email);
        labelTelefoneAtual.setText(telefone);
    }

    @FXML
    private void handleAtualizar() {
        String novaSenha = campoNovaSenha.getText();
        String senhaAtual = campoSenhaAtual.getText();
        String novoEmail = campoNovoEmail.getText().trim();
        String novoTelefone = campoNovoTelefone.getText().trim();

        if (senhaAtual.isEmpty() || novaSenha.isEmpty() || novoEmail.isEmpty() || novoTelefone.isEmpty()) {
            mensagemLabel.setText("Preencha todos os campos.");
            return;
        }

        boolean sucesso = db.atualizarDadosUsuario(emailOriginal, senhaAtual, novoEmail, novaSenha, novoTelefone);

        if (sucesso) {
            mensagemLabel.setText("Dados atualizados com sucesso!");
        } else {
            mensagemLabel.setText("Erro ao atualizar. Verifique a senha atual.");
        }
    }

    @FXML
    private void handleVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/MainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) campoNovaSenha.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu Principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mensagemLabel.setText("Erro ao voltar ao menu.");
        }
    }
}
