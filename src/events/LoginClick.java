package events;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.services.LoginService;
import entity.LoginMessage;
import entity.StatusCode;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import application.CardReader;

public class LoginClick implements EventHandler<ActionEvent> {
    private final PasswordField password;
    private final ComboBox<String> cardTerminals;
    private ProgressBar progressBar;
    private LoginService loginService;
    private ActionEvent actionEvent;

    public LoginClick(PasswordField password, ComboBox<String> cardTerminals, ProgressBar progressBar, LoginService loginService) {
        this.password = password;
        this.cardTerminals = cardTerminals;
        this.progressBar = progressBar;
        this.loginService = loginService;

        this.progressBar.visibleProperty().bind(loginService.runningProperty());

        loginService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                LoginMessage result = loginService.getValue();   //here you get the return value of your service
                System.out.println(result.getStatusCode());
                System.out.println("Response: " + result.getStatusCode());
                if (result.getStatusCode() == StatusCode.OK)
                    switchScene(actionEvent);
                else {
                    System.out.println("Wrong pin!!!");
                }
            }
        });

        loginService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                System.out.println("Failed");
            }
        });
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            this.actionEvent = event;
            if (cardTerminals.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            CardReader.cardReader = cardTerminals.getSelectionModel().getSelectedItem();
            LoginMessage loginMessage = new LoginMessage();
            loginMessage.setCardReader(cardTerminals.getSelectionModel().getSelectedItem());
            loginMessage.setUserPin(password.getText());

            loginService.setLoginMessage(loginMessage);
            loginService.restart();
        } catch (Exception ex) {
            showException(ex);
            Logger.getLogger(LoginClick.class.getName()).log(Level.SEVERE, null, ex);
        }

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
            showException(ex);
            Logger.getLogger(LoginClick.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void showException(Exception ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Look, an Exception Dialog");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
}
