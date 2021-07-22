package events;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.ServiceExample;
import application.SmartCardCommunication;
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

import javax.smartcardio.*;

public class LoginClick implements EventHandler<ActionEvent> {
    private final PasswordField password;
    private final ComboBox<String> cardTerminals;
    private ProgressBar progressBar;
    private ServiceExample serviceExample;
    private ActionEvent actionEvent;

    public LoginClick(PasswordField password, ComboBox<String> cardTerminals, ProgressBar progressBar, ServiceExample serviceExample) {
        this.password = password;
        this.cardTerminals = cardTerminals;
        this.progressBar = progressBar;
        this.serviceExample = serviceExample;

        progressBar.visibleProperty().bind(serviceExample.runningProperty());
        serviceExample.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                String result = serviceExample.getValue();   //here you get the return value of your service
                System.out.println(result);
                if(result.equals("OK"))
                    switchScene(actionEvent);
                else {
                    System.out.println("Wrong pin!!!");
                }
            }
        });

        serviceExample.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                System.out.println("Failed");
            }
        });
    }

    @Override
    public void handle(ActionEvent event) {
        this.actionEvent = event;
        String pin = password.getText();
        if (login(pin)) {
            //switchScene(event);
        }
    }

    private void switchScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../application/fxml/MainScene.fxml"));
            Parent mainRoot = (Parent) loader.load();
            //InputStream stream = getClass().getResourceAsStream("/fxml/Scene.fxml");
            Scene mainScene = new Scene(mainRoot);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(mainScene);
            window.show();
        } catch (Exception ex) {
            showException(ex);
            Logger.getLogger(LoginClick.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean login(String pinString) {
        try {
            CardReader.cardReader = cardTerminals.getSelectionModel().getSelectedItem();
            serviceExample.setPinString(pinString);
            serviceExample.restart(); //here you start your service
            return true;
        } catch (Exception ex) {
            showException(ex);
            Logger.getLogger(LoginClick.class.getName()).log(Level.SEVERE, null, ex);
            return false;
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
