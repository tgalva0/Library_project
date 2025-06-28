package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.database.Bibliotecario;
import main.database.DatabaseAPI;

import java.util.Optional;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private Label mensagem;
    private final DatabaseAPI db = new DatabaseAPI();



    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String senha = senhaField.getText();

        Optional<Bibliotecario> usuario = db.autenticarBibliotecario(email, senha);
        if (usuario.isPresent()) {
            AppLoop_GUI.getInstance().setUsuarioLogado(usuario.orElse(null));
            AppLoop_GUI.getInstance().mostrarMenuPrincipal();
        } else {
            // alerta de erro
        }
    }
}
