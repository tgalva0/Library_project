package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        AppLoop_GUI.iniciar(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
