package controllers;

import application.SmartCardCommunication;
import entity.UserAccount;
import events.SaveToCardClick;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    @FXML
    private TableView<UserAccount> tableUserAccounts;
    @FXML
    private TableColumn<UserAccount, String> columnUserName;
    @FXML
    private TableColumn<UserAccount, String> columnPassword;
    @FXML
    private Button btnAddNewAccount;
    @FXML
    private Button btnDeleteAccount;
    @FXML
    private Button btnSaveToCard;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            columnUserName.setCellValueFactory(new PropertyValueFactory<>("username"));
            columnUserName.setCellFactory(TextFieldTableCell.forTableColumn());
            columnUserName.setOnEditCommit(event -> event.getRowValue().setUsername(event.getNewValue()));
            columnPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
            columnPassword.setCellFactory(TextFieldTableCell.forTableColumn());
            columnPassword.setOnEditCommit(event -> event.getRowValue().setPassword(event.getNewValue()));

            ObservableList<UserAccount> data = FXCollections.observableArrayList();
            data.add(new UserAccount("djordje0065@gmail.com", "123pass"));
            data.add(new UserAccount("test@gmail.com", "12345678"));
            data.add(new UserAccount("skype@gmail.com", "fxffsxxa"));
            tableUserAccounts.setItems(data);

            btnAddNewAccount.setOnAction(event -> {
                // Create Dialog form
                Dialog<Pair<String, String>> dialog = new Dialog<>();
                dialog.setTitle("Add new account");
                ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
                GridPane gridPane = new GridPane();
                gridPane.setHgap(10);
                gridPane.setVgap(10);
                gridPane.setPadding(new Insets(20, 150, 10, 10));
                TextField txtUserName = new TextField();
                txtUserName.setPromptText("Username");
                TextField txtPassword = new TextField();
                txtPassword.setPromptText("Password");
                gridPane.add(new Label("Username: "), 0, 0);
                gridPane.add(txtUserName, 1, 0);
                gridPane.add(new Label("Password: "), 0, 1);
                gridPane.add(txtPassword, 1, 1);
                dialog.getDialogPane().setContent(gridPane);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == okButtonType) {
                        tableUserAccounts.getItems().add(new UserAccount(txtUserName.getText(), txtPassword.getText()));
                    }
                    return null;
                });
                dialog.showAndWait();
            });

            btnDeleteAccount.setOnAction(event -> {
                ObservableList<UserAccount> sel, items;
                items = tableUserAccounts.getItems();
                sel = tableUserAccounts.getSelectionModel().getSelectedItems();
                for (UserAccount m : sel)
                    items.remove(m);
            });

            btnSaveToCard.setOnAction(new SaveToCardClick());

        } catch (Exception ex) {

        }
    }


}
