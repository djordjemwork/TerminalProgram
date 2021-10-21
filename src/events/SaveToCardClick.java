package events;

import application.Util;
import application.services.SaveToCardCardService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.StatusCode;
import entity.UserAccount;
import entity.UserAccountMessage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaveToCardClick implements EventHandler<ActionEvent> {

    private final List<UserAccount> accountList;
    private final SaveToCardCardService saveToCardCardService;

    public SaveToCardClick(ProgressBar progressBarMain, TableView<UserAccount> tableUserAccounts, SaveToCardCardService saveToCardCardService) {
        this.accountList = tableUserAccounts.getItems();
        this.saveToCardCardService = saveToCardCardService;
        progressBarMain.visibleProperty().bind(saveToCardCardService.runningProperty());

        this.saveToCardCardService.setOnSucceeded(workerStateEvent -> {
            UserAccountMessage result = this.saveToCardCardService.getValue();   //here you get the return value of your service
            if (result.getStatusCode() == StatusCode.OK) {
                Util.showSuccessfulDialog("Data has been saved successfully");
                tableUserAccounts.getItems().forEach(e -> e.setSavedToCard(true));
                tableUserAccounts.refresh();
            }
        });

        this.saveToCardCardService.setOnFailed(workerStateEvent -> System.out.println("Error"));
    }

    @Override
    public void handle(ActionEvent event) {

        try {
            UserAccountMessage userAccountMessage = new UserAccountMessage();
            userAccountMessage.setResponse("OK");

            String userAccountsJSON = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create().toJson(accountList);

            userAccountMessage.setUserAccountList(userAccountsJSON);
            saveToCardCardService.setUserAccountMessage(userAccountMessage);
            saveToCardCardService.restart();
        } catch (Exception ex) {
            //showException(ex);
            Logger.getLogger(LoginClick.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error handle");
        }

    }
}
