package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.database.Bibliotecario;

import java.io.IOException;

public class MainMenuController {

    @FXML private Button buscarPorTituloButton;
    @FXML private Button buscarPorAutorButton;
    @FXML private Button buscarMembroButton;
    @FXML private Button buscarEmprestimoButton;
    @FXML private Button registrarEmprestimoButton;
    @FXML private Button devolverEmprestimoButton;
    @FXML private Button registrarLivroButton;
    @FXML private Button registrarMembroButton;
    @FXML private Button removerCopiaButton;
    @FXML private Button removerMembroButton;
    @FXML private Button atualizarMembroButton;
    @FXML private Button logoutButton;
    @FXML private Label nomeLabel;

    @FXML
    public void initialize() {
        Bibliotecario usuario = AppLoop_GUI.getInstance().getUsuarioLogado();
        if (usuario != null) {
            nomeLabel.setText("Olá, " + usuario.getNome() + "!");
        }
    }

    @FXML
    private void handleBuscarPorTitulo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/BuscarLivroPorTitulo.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Buscar Livro por Título");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível abrir a tela de busca");
            alert.setContentText("Verifique se o arquivo BuscarLivroPorTitulo.fxml está no caminho correto.");
            alert.showAndWait();
        }

    }

    @FXML
    private void handleBuscarPorAutor(ActionEvent event) {
        showPlaceholder("Procurar Livro por Autor");
    }

    @FXML
    private void handleBuscarMembro(ActionEvent event) {
        showPlaceholder("Procurar Membro por Email");
    }

    @FXML
    private void handleBuscarEmprestimo(ActionEvent event) {
        showPlaceholder("Procurar Empréstimo por Email");
    }

    @FXML
    private void handleRegistrarEmprestimo(ActionEvent event) {
        showPlaceholder("Registrar Empréstimo");
    }

    @FXML
    private void handleDevolverEmprestimo(ActionEvent event) {
        showPlaceholder("Devolver Empréstimo");
    }

    @FXML
    private void handleRegistrarLivro(ActionEvent event) {
        showPlaceholder("Registrar Livro");
    }

    @FXML
    private void handleRegistrarMembro(ActionEvent event) {
        showPlaceholder("Registrar Membro");
    }

    @FXML
    private void handleRemoverCopia(ActionEvent event) {
        showPlaceholder("Remover Cópias de Livro");
    }

    @FXML
    private void handleRemoverMembro(ActionEvent event) {
        showPlaceholder("Remover Membro");
    }

    @FXML
    private void handleAtualizarMembro(ActionEvent event) {
        showPlaceholder("Atualizar Membro");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Carrega a tela de login novamente
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/login.fxml"));
            Parent root = loader.load();

            // Obtém o stage atual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Define a nova cena (login)
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível fazer logout");
            alert.setContentText("Verifique se o arquivo login.fxml está no caminho correto.");
            alert.showAndWait();
        }

        // Aqui você pode redirecionar para a tela de login
    }

    private void showPlaceholder(String actionName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Função não implementada");
        alert.setHeaderText(null);
        alert.setContentText("Você clicou em: " + actionName + "\n\nEssa funcionalidade será implementada em breve!");
        alert.showAndWait();
    }
}