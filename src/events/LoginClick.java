package events;

import application.CardReader;
import application.Util;
import application.services.LoginService;
import entity.LoginMessage;
import entity.StatusCode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

import static application.Util.showException;

public class LoginClick implements EventHandler<ActionEvent> {
    private final PasswordField password;
    private final ComboBox<String> cardTerminals;
    private final LoginService loginService;
    private ActionEvent actionEvent;

    public LoginClick(PasswordField password, ComboBox<String> cardTerminals, ProgressBar progressBar, LoginService loginService) {
        this.password = password;
        this.cardTerminals = cardTerminals;
        this.loginService = loginService;
        progressBar.visibleProperty().bind(loginService.runningProperty());

        this.loginService.setOnSucceeded(workerStateEvent -> {
            LoginMessage result = this.loginService.getValue();   //here you get the return value of your service

            if (result.getStatusCode() == StatusCode.OK)
                switchScene(actionEvent);
        });

        this.loginService.setOnFailed(workerStateEvent -> Util.showException((Exception) workerStateEvent.getSource().getException()));
    }

    @Override
    public void handle(ActionEvent event) {
        if (cardTerminals.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        this.actionEvent = event;

        CardReader.cardReader = cardTerminals.getSelectionModel().getSelectedItem();
        LoginMessage loginMessage = new LoginMessage();
        loginMessage.setCardReader(cardTerminals.getSelectionModel().getSelectedItem());
        loginMessage.setUserPin(password.getText());

        loginService.setLoginMessage(loginMessage);
        loginService.restart();
    }

    private void switchScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../application/fxml/MainScene.fxml"));
            Parent mainRoot = (Parent) loader.load();
            Scene mainScene = new Scene(mainRoot);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(mainScene);
            window.show();
        } catch (Exception ex) {
            Util.showException(ex);
            Logger.getLogger(LoginClick.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
