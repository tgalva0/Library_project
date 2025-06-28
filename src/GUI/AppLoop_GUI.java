package GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.database.Bibliotecario;

import java.io.IOException;

public class AppLoop_GUI {

    private static AppLoop_GUI instance;
    private Stage primaryStage;
    private Bibliotecario usuarioLogado;

    private AppLoop_GUI(Stage stage) {
        this.primaryStage = stage;
    }

    public static void iniciar(Stage stage) {
        if (instance == null) {
            instance = new AppLoop_GUI(stage);
        }
        instance.mostrarTelaLogin();
    }

    public static AppLoop_GUI getInstance() {
        return instance;
    }

    public void setUsuarioLogado(Bibliotecario usuario) {
        this.usuarioLogado = usuario;
    }

    public Bibliotecario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void mostrarTelaLogin() {
        carregarTela("/GUI/login.fxml", "Login");
    }

    public void mostrarMenuPrincipal() {
        carregarTela("/GUI/MainMenu.fxml", "Menu Principal");
    }


    private void carregarTela(String caminhoFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle(titulo);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Você pode exibir um alerta aqui se quiser
        }
    }
}