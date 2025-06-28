package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.database.Livro;
import main.database.DatabaseAPI;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class BuscarLivroPorAutorController {

    @FXML private TextField campoBuscaAutor;
    @FXML private TableView<Livro> tabelaLivrosAutor;
    @FXML private TableColumn<Livro, String> colTitulo;
    @FXML private TableColumn<Livro, String> colISBN;
    @FXML private TableColumn<Livro, Integer> colCopias;
    @FXML private TableColumn<Livro, String> colAutor;
    private DatabaseAPI db = new DatabaseAPI();

    private final ObservableList<Livro> livros = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCopias.setCellValueFactory(new PropertyValueFactory<>("num_copias"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));

        tabelaLivrosAutor.setItems(livros);

        campoBuscaAutor.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() >= 3) {
                db.buscarLivrosPorAutor(newText).ifPresentOrElse(
                        resultado -> livros.setAll(resultado),
                        livros::clear
                );
            } else {
                livros.clear();
            }
        });

        tabelaLivrosAutor.setRowFactory(tv -> {
            TableRow<Livro> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem excluirItem = new MenuItem("Excluir Livro");

            excluirItem.setOnAction(event -> {
                Livro livroSelecionado = row.getItem();
                if (livroSelecionado != null) {
                    boolean sucesso = db.excluirCopiasLivro(
                            livroSelecionado.getTitulo(),
                            livroSelecionado.getIsbn(),
                            livroSelecionado.getNum_copias()
                    );

                    Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.setTitle("Resultado da Exclusão");

                    if (sucesso) {
                        livros.remove(livroSelecionado);
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

    @FXML
    private void handleVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/MainMenu.fxml"));
            Stage stage = (Stage) campoBuscaAutor.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Menu Principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");
            alerta.setHeaderText("Não foi possível voltar ao menu");
            alerta.setContentText("Verifique o caminho do arquivo MainMenu.fxml.");
            alerta.showAndWait();
        }
    }
}