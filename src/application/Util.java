package application;

import javafx.scene.control.Alert;

public class Util {

    public final static String appletID = "A0000002481101";

    public static void showException(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("There was a problem");
        alert.setContentText(ex.getCause().getMessage());
        alert.show();
    }

    public static void showSuccessfulDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operation status");
        alert.setHeaderText(message);

        alert.show();
    }
}
