package controllers;

import application.ServiceExample;
import application.SmartCardCommunication;
import javafx.fxml.Initializable;
import events.LoginClick;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class LoginController implements Initializable {
    @FXML
    private Button buttonLogin;
    @FXML
    private PasswordField password;
    @FXML
    private ComboBox<String> cardReaders;
    @FXML
    private ProgressBar progressBar;
    private ServiceExample serviceExample;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            SmartCardCommunication communication = SmartCardCommunication.getInstance();
            communication.loadCardReaders(cardReaders);
            serviceExample = new ServiceExample();
            buttonLogin.setOnAction(new LoginClick(password, cardReaders, progressBar, serviceExample));
        } catch (CardException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
