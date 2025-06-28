package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import main.database.DatabaseAPI;
import main.database.Livro;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class BuscarLivroUnificadoController {

    @FXML private ComboBox<String> comboTipoBusca;
    @FXML private TextField campoBusca;
    @FXML private TableView<Livro> tabelaLivros;
    @FXML private TableColumn<Livro, String> colTitulo;
    @FXML private TableColumn<Livro, String> colAutor;
    @FXML private TableColumn<Livro, String> colISBN;

    private final ObservableList<Livro> livros = FXCollections.observableArrayList();
    private final DatabaseAPI db = new DatabaseAPI();

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor")); //
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tabelaLivros.setItems(livros);

        campoBusca.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 3 && comboTipoBusca.getValue() != null) {
                buscarLivros(comboTipoBusca.getValue(), newVal);
            } else {
                livros.clear();
            }
        });

        tabelaLivros.setRowFactory(tv -> {
            TableRow<Livro> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem excluirItem = new MenuItem("Excluir Livro");

            excluirItem.setOnAction(event -> {
                Livro livro = row.getItem();
                if (livro != null) {
                    boolean sucesso = db.excluirCopiasLivro(
                            livro.getTitulo(),
                            livro.getIsbn(),
                            livro.getNum_copias()
                    );

                    Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.setTitle("Resultado da Exclusão");

                    if (sucesso) {
                        livros.remove(livro);
                        alerta.setHeaderText("Livro excluído com sucesso!");
                    } else {
                        alerta.setHeaderText("Erro ao excluir o livro.");
                    }

                    alerta.showAndWait();
                }
            });

            contextMenu.getItems().add(excluirItem);
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    private void buscarLivros(String tipo, String termo) {
        Optional<List<Livro>> resultado;
        if (tipo.equals("Título")) {
            resultado = db.buscarLivrosPorTituloInicial(termo);
        } else {
            resultado = db.buscarLivrosPorAutor(termo);
        }

        livros.setAll(resultado.get());
    }

    @FXML
    private void handleVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/MainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) campoBusca.getScene().getWindow();
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
}