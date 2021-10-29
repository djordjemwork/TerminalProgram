package application;

import controllers.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/LoginScene.fxml"));
            Parent root = loader.load();
            Scene loginScene = new Scene(root);

            stage.setTitle("Password Management");
            stage.setScene(loginScene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
