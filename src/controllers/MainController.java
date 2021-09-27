package controllers;

import application.SmartCardCommunication;
import entity.UserAccount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            columnUserName.setCellValueFactory(new PropertyValueFactory<>("username"));
            columnPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
            ObservableList<UserAccount> data = FXCollections.observableArrayList();
            data.add(new UserAccount("djordje0065@gmail.com", "123pass"));
            data.add(new UserAccount("test@gmail.com", "12345678"));
            data.add(new UserAccount("skype@gmail.com", "fxffsxxa"));


            tableUserAccounts.setItems(data);
        } catch (Exception ex) {

        }
    }
}
