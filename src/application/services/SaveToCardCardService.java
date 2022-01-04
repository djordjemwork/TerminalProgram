package application.services;

import application.SmartCardCommunication;
import entity.StatusCode;
import entity.UserAccountMessage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SaveToCardCardService extends Service<UserAccountMessage> {

    private UserAccountMessage userAccountMessage;

    public SaveToCardCardService() {

    }

    public UserAccountMessage getUserAccountMessage() {
        return userAccountMessage;
    }

    public void setUserAccountMessage(UserAccountMessage userAccountMessage) {
        this.userAccountMessage = userAccountMessage;
    }

    @Override
    protected Task<UserAccountMessage> createTask() {
        return new Task<UserAccountMessage>() {
            @Override
            protected UserAccountMessage call() throws Exception {
                SmartCardCommunication communication = SmartCardCommunication.getInstance();
                communication.putUserAccountDataToCard(userAccountMessage);
                return userAccountMessage;
            }
        };
    }
}
