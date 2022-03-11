package events;

import application.Util;
import application.services.SaveToCardCardService;
import com.google.gson.GsonBuilder;
import application.entity.StatusCode;
import application.entity.UserAccount;
import application.entity.UserAccountMessage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;

import java.util.List;

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
                tableUserAccounts.getSelectionModel().clearSelection();
                tableUserAccounts.refresh();
            }
        });

        this.saveToCardCardService.setOnFailed(workerStateEvent -> {
            Util.showException((Exception) workerStateEvent.getSource().getException());
        });
    }

    @Override
    public void handle(ActionEvent event) {
        UserAccountMessage userAccountMessage = new UserAccountMessage();
        userAccountMessage.setResponse("OK");

        String userAccountsJSON = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create().toJson(accountList);

        userAccountMessage.setUserAccountList(userAccountsJSON);
        saveToCardCardService.setUserAccountMessage(userAccountMessage);
        saveToCardCardService.restart();
    }
}
