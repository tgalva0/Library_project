package GUI;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.stage.Stage;
import main.database.Livro;
import main.database.DatabaseAPI;

import java.io.IOException;

public class BuscarLivroPorTituloController {

    @FXML private TextField campoBusca;
    @FXML private TableView<Livro> tabelaLivros;
    @FXML private TableColumn<Livro, String> colTitulo;
    @FXML private TableColumn<Livro, String> colISBN;
    @FXML private TableColumn<Livro, Integer> colCopias;
    @FXML private TableColumn<Livro, String> colAutor;
    private DatabaseAPI db = new DatabaseAPI();

    private final ObservableList<Livro> livros = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitulo()));
        colISBN.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getIsbn()));
        colCopias.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNum_copias()));
        colAutor.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getAutor()));

        tabelaLivros.setItems(livros);

        campoBusca.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 3) {
                db.buscarLivrosPorTituloInicial(newVal).ifPresentOrElse(
                        resultado -> livros.setAll(resultado),
                        livros::clear
                );
            } else {
                livros.clear();
            }
        });
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/MainMenu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu Principal");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Não foi possível voltar ao menu");
            alert.setContentText("Verifique se o arquivo MainMenu.fxml está no caminho correto.");
            alert.showAndWait();
        }
    }
}