package controllers;

import application.services.ReadDataFromCardService;
import application.services.SaveToCardCardService;
import com.sun.rowset.internal.Row;
import entity.UserAccount;
import events.ReadDataFromCard;
import events.SaveToCardClick;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
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
    @FXML
    private ProgressBar progressBarMain;
    @FXML
    private TextField txtSearchTable;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            columnUserName.setCellValueFactory(new PropertyValueFactory<>("username"));
            columnUserName.setCellFactory(TextFieldTableCell.forTableColumn());
            columnPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
            columnPassword.setCellFactory(TextFieldTableCell.forTableColumn());

            ReadDataFromCardService readDataFromCardService = new ReadDataFromCardService();
            ReadDataFromCard readDataFromCard = new ReadDataFromCard(readDataFromCardService, tableUserAccounts);
            readDataFromCard.handle(new ActionEvent());

            btnDeleteAccount.setOnAction(event -> {
                System.out.println("Delete...");
                ObservableList<UserAccount> sel, items;
                items = tableUserAccounts.getItems();
                sel = tableUserAccounts.getSelectionModel().getSelectedItems();
                for (UserAccount m : sel)
                    items.remove(m);
                tableUserAccounts.getSelectionModel().clearSelection();
            });

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
                        tableUserAccounts.getItems().add(new UserAccount(txtUserName.getText(), txtPassword.getText(), false));
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
                    if (item != null && item.getUsername().equals(" ")) {
                        tableUserAccounts.getSelectionModel().select(tableUserAccounts.getItems().get(tableUserAccounts.getItems().size() - 1));
                        btnDeleteAccount.fire();
                    }
                    if (item != null)
                        setStyle("");
                    if (item != null && !item.isSavedToCard())
                        setStyle("-fx-text-background-color: red;");
                }
            });


        } catch (Exception ex) {

        }
    }


}
