package controllers;

import application.services.ReadDataFromCardService;
import application.services.SaveToCardCardService;
import entity.UserAccount;
import events.ReadDataFromCard;
import events.SaveToCardClick;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.ResourceBundle;

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
    @FXML
    private ProgressBar progressBarMain;
    @FXML
    private CheckBox checkShowPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnUserName.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnUserName.setCellFactory(TextFieldTableCell.forTableColumn());
        columnPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        columnPassword.setCellFactory(TextFieldTableCell.forTableColumn());

        ReadDataFromCardService readDataFromCardService = new ReadDataFromCardService();
        ReadDataFromCard readDataFromCard = new ReadDataFromCard(readDataFromCardService, tableUserAccounts);
        readDataFromCard.handle(new ActionEvent());

        btnDeleteAccount.setOnAction(event -> {
            ObservableList<UserAccount> sel, items;
            items = tableUserAccounts.getItems();
            sel = tableUserAccounts.getSelectionModel().getSelectedItems();
            for (UserAccount m : sel)
                items.remove(m);
            tableUserAccounts.getSelectionModel().clearSelection();
        });

        btnAddNewAccount.setOnAction(event -> {
            // Create Dialog form
            tableUserAccounts.getSelectionModel().clearSelection();
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
                    tableUserAccounts.getItems().add(new UserAccount(txtUserName.getText(),
                            checkShowPassword.isSelected() ? txtPassword.getText() : "************", false,
                            txtPassword.getText()));
                }
                return null;
            });
            dialog.showAndWait();
        });

        SaveToCardCardService saveToCardCardService = new SaveToCardCardService();
        btnSaveToCard.setOnAction(new SaveToCardClick(progressBarMain, tableUserAccounts, saveToCardCardService));

        tableUserAccounts.setRowFactory((tv) -> new TableRow<UserAccount>() {
            @Override
            public void updateItem(UserAccount item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setStyle("");
                }
                if (item != null && !item.isSavedToCard()) {
                    setStyle("-fx-text-background-color: red;");
                }
            }
        });

        checkShowPassword.setOnAction(event -> {
            tableUserAccounts.getSelectionModel().clearSelection();
            if (checkShowPassword.isSelected()) {
                tableUserAccounts.getItems().forEach(e -> e.setPassword(e.getPasswordToStore()));
            } else {
                tableUserAccounts.getItems().forEach(e -> e.setPassword("************"));
            }
            tableUserAccounts.refresh();
        });
    }


}
