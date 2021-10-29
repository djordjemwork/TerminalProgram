package events;

import application.services.ReadDataFromCardService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.StatusCode;
import entity.UserAccount;
import entity.UserAccountMessage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;

import java.util.List;

public class ReadDataFromCard implements EventHandler<ActionEvent> {

    private final ReadDataFromCardService readDataFromCardService;

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
            // TODO: Handle exception
            System.out.println(readDataFromCardService);
        });

    }

    @Override
    public void handle(ActionEvent event) {
        UserAccountMessage userAccountMessage = new UserAccountMessage();
        userAccountMessage.setResponse("OK");
        readDataFromCardService.setUserAccountMessage(userAccountMessage);
        readDataFromCardService.restart();

    }
}
