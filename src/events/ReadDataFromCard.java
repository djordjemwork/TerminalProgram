package events;

import application.services.ReadDataFromCardService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.StatusCode;
import entity.UserAccount;
import entity.UserAccountMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadDataFromCard implements EventHandler<ActionEvent> {

    private final ReadDataFromCardService readDataFromCardService;
    private List<UserAccount> userAccountList;

    public ReadDataFromCard(ReadDataFromCardService readDataFromCardService, TableView<UserAccount> tableUserAccounts) {
        this.readDataFromCardService = readDataFromCardService;
        this.readDataFromCardService.setOnSucceeded(workerStateEvent -> {
            UserAccountMessage result = this.readDataFromCardService.getValue();
            System.out.println("Response read from card: " + result.getStatusCode());
            String userAccountJSON = result.getUserAccountList();
            if (result.getStatusCode() != StatusCode.OK || userAccountJSON == null || userAccountJSON.equals("")) {
                return;
            }
            List<UserAccount> userAccountListTest = new Gson().fromJson(userAccountJSON, new TypeToken<List<UserAccount>>() {
            }.getType());
            userAccountListTest.forEach(e -> e.setSavedToCard(true));
            userAccountListTest.forEach(e -> e.setPassword("************"));
            tableUserAccounts.getItems().addAll(userAccountListTest);
            tableUserAccounts.getSelectionModel().clearSelection();
        });

        this.readDataFromCardService.setOnFailed(workerStateEvent -> {
            System.out.println("error");
        });

    }

    @Override
    public void handle(ActionEvent event) {
        try {
            UserAccountMessage userAccountMessage = new UserAccountMessage();
            userAccountMessage.setResponse("OK");
            readDataFromCardService.setUserAccountMessage(userAccountMessage);
            readDataFromCardService.restart();
        } catch (Exception ex) {
            Logger.getLogger(LoginClick.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error handle");
        }
    }
}
