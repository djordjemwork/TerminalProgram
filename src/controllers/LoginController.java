package controllers;

import application.SmartCardCommunication;
import application.services.LoginService;
import events.LoginClick;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button buttonLogin;
    @FXML
    private PasswordField password;
    @FXML
    private ComboBox<String> cardReaders;
    @FXML
    private ProgressBar progressBar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SmartCardCommunication communication = SmartCardCommunication.getInstance();
        communication.loadCardReaders(cardReaders);
        buttonLogin.setOnAction(new LoginClick(password, cardReaders, progressBar, new LoginService()));

    }


}
