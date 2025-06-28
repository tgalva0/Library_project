package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import main.database.Membro;
import main.database.DatabaseAPI;

import java.io.IOException;
import java.util.Optional;

public class BuscarMembroPorEmailController {

    @FXML private TextField campoEmail;
    @FXML private Label labelNome;
    @FXML private Label labelTelefone;
    @FXML private Label labelPapel;
    private DatabaseAPI db = new DatabaseAPI();

    @FXML
    private void handleBuscarMembro() {
        String email = campoEmail.getText().trim();

        if (email.isEmpty()) {
            mostrarAlerta("Por favor, digite um e-mail.");
            return;
        }

        Optional<Membro> resultado = db.buscarMembroPorEmail(email);

        if (resultado.isPresent()) {
            Membro membro = resultado.get();
            labelNome.setText("Nome: " + membro.getNome());
            labelTelefone.setText("Telefone: " + membro.getTelefone());
            labelPapel.setText("Papel: " + membro.getPapel().toString());

            ContextMenu contextMenu = new ContextMenu();

            MenuItem excluirItem = new MenuItem("Excluir Membro");
            excluirItem.setOnAction(evt -> {
                boolean sucesso = db.excluirMembro(email);
                if (sucesso) {
                    mostrarAlerta("Membro excluído com sucesso.");
                    campoEmail.clear();
                    limparLabels();
                } else {
                    mostrarAlerta("Erro ao excluir membro.");
                }
            });

            MenuItem atualizarItem = new MenuItem("Atualizar Dados do Membro");
            atualizarItem.setOnAction(evt -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/AtualizarMembro.fxml"));
                    Parent root = loader.load();

                    AtualizarMembroController controller = loader.getController();
                    controller.setDadosAtuais(email, resultado.get().getTelefone());

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Atualizar Membro");
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro ao abrir tela de atualização.");
                }
            });

            contextMenu.getItems().addAll(excluirItem, atualizarItem);
            labelNome.setContextMenu(contextMenu);
            labelTelefone.setContextMenu(contextMenu);
            labelPapel.setContextMenu(contextMenu);


        } else {
            mostrarAlerta("Membro não encontrado.");
            limparLabels();
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

    private void limparLabels() {
        labelNome.setText("Nome: ");
        labelTelefone.setText("Telefone: ");
        labelPapel.setText("Papel: ");
    }
}