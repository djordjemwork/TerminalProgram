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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadDataFromCard implements EventHandler<ActionEvent> {

    private final ReadDataFromCardService readDataFromCardService;


    public ReadDataFromCard(ReadDataFromCardService readDataFromCardService, TableView<UserAccount> tableUserAccounts) {
        this.readDataFromCardService = readDataFromCardService;

        this.readDataFromCardService.setOnSucceeded(workerStateEvent -> {
            UserAccountMessage result = this.readDataFromCardService.getValue();
            System.out.println("Response read from card: " + result.getStatusCode());
            if(result.getStatusCode() == StatusCode.OK) {
                String userAccountJSON = result.getUserAccountList();
                List<UserAccount> userAccountListTest = new Gson().fromJson(userAccountJSON, new TypeToken<List<UserAccount>>() {}.getType());

                tableUserAccounts.getItems().addAll(userAccountListTest);
                tableUserAccounts.getSelectionModel().clearSelection();
            }
        });

        this.readDataFromCardService.setOnFailed(workerStateEvent -> System.out.println("Error"));

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
