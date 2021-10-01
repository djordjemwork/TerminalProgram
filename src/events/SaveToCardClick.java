package events;

import application.CardReader;
import application.services.SaveToCardCardService;
import entity.LoginMessage;
import entity.StatusCode;
import entity.UserAccount;
import entity.UserAccountMessage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaveToCardClick implements EventHandler<ActionEvent> {

    private ProgressBar progressBarMain;
    private List<UserAccount> accountList;
    private SaveToCardCardService saveToCardCardService;

    public SaveToCardClick(ProgressBar progressBarMain, List<UserAccount> accountList, SaveToCardCardService saveToCardCardService) {
        this.progressBarMain = progressBarMain;
        this.accountList = accountList;
        this.saveToCardCardService = saveToCardCardService;
        this.progressBarMain.visibleProperty().bind(saveToCardCardService.runningProperty());

        this.saveToCardCardService.setOnSucceeded(workerStateEvent -> {
            UserAccountMessage result = this.saveToCardCardService.getValue();   //here you get the return value of your service
            System.out.println(result.getStatusCode());
            System.out.println("Response: " + result.getStatusCode());

            if (result.getStatusCode() == StatusCode.OK) {;
            } else {
                System.out.println("Wrong pin!!!");
            }
        });

        this.saveToCardCardService.setOnFailed(workerStateEvent -> System.out.println("Error"));
    }

    @Override
    public void handle(ActionEvent event) {

        try {
            UserAccountMessage userAccountMessage = new UserAccountMessage();
            saveToCardCardService.setUserAccountMessage(userAccountMessage);
            saveToCardCardService.restart();
        } catch (Exception ex) {
            //showException(ex);
            Logger.getLogger(LoginClick.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}