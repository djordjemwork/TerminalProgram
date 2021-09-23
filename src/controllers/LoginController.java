package controllers;

import application.services.LoginService;
import application.SmartCardCommunication;
import javafx.fxml.Initializable;
import events.LoginClick;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.smartcardio.CardException;

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
        try {
            SmartCardCommunication communication = SmartCardCommunication.getInstance();
            communication.loadCardReaders(cardReaders);
            LoginService loginService = new LoginService();
            buttonLogin.setOnAction(new LoginClick(password, cardReaders, progressBar, loginService));
        } catch (Exception ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
