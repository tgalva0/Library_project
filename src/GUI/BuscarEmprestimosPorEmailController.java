package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import main.database.DatabaseAPI;
import main.database.Emprestimo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class BuscarEmprestimosPorEmailController {

    @FXML private TextField campoEmail;
    @FXML private TableView<Emprestimo> tabelaEmprestimos;
    @FXML private TableColumn<Emprestimo, String> colTitulo;
    @FXML private TableColumn<Emprestimo, String> colDataEmprestimo;
    @FXML private TableColumn<Emprestimo, String> colDataDevolucao;
    @FXML private TableColumn<Emprestimo, String> colStatus;
    @FXML private TableColumn<Emprestimo, Double> colMulta;

    private final ObservableList<Emprestimo> emprestimos = FXCollections.observableArrayList();
    private final DatabaseAPI db = new DatabaseAPI();

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colDataEmprestimo.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimoFormatada"));
        colDataDevolucao.setCellValueFactory(new PropertyValueFactory<>("dataDevolucaoFormatada"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusEmprestimo"));
        colMulta.setCellValueFactory(new PropertyValueFactory<>("multa"));

        tabelaEmprestimos.setItems(emprestimos);

        tabelaEmprestimos.setRowFactory(tv -> {
            TableRow<Emprestimo> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem darBaixaItem = new MenuItem("Dar baixa no empréstimo");

            darBaixaItem.setOnAction(event -> {
                Emprestimo emprestimo = row.getItem();
                if (emprestimo != null) {
                    boolean sucesso = db.darBaixaEmprestimo(emprestimo.getEmailMembro(), emprestimo.getTitulo());

                    Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.setTitle("Resultado da Baixa");

                    if (sucesso) {
                        alerta.setHeaderText("Empréstimo finalizado com sucesso!");
                        atualizarTabela(emprestimo.getEmailMembro());
                    } else {
                        alerta.setHeaderText("Erro ao dar baixa no empréstimo.");
                    }

                    alerta.showAndWait();
                }
            });

            contextMenu.getItems().add(darBaixaItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    @FXML
    private void handleBuscar() {
        String email = campoEmail.getText().trim();
        if (email.isEmpty()) {
            mostrarAlerta("Digite um e-mail para buscar.");
            return;
        }

        Optional<List<Emprestimo>> resultado = db.buscarEmprestimosPorEmail(email);
        if (resultado.isPresent() && !resultado.get().isEmpty()) {
            emprestimos.setAll(resultado.get());
        } else {
            emprestimos.clear();
            mostrarAlerta("Nenhum empréstimo encontrado para este e-mail.");
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

    private void atualizarTabela(String email) {
        Optional<List<Emprestimo>> resultado = db.buscarEmprestimosPorEmail(email);
        if (resultado.isPresent()) {
            emprestimos.setAll(resultado.get());
        } else {
            emprestimos.clear();
        }
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}