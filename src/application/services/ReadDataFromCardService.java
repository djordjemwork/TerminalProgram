package application.services;

import application.SmartCardCommunication;
import entity.StatusCode;
import entity.UserAccountMessage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ReadDataFromCardService extends Service<UserAccountMessage> {
    private UserAccountMessage userAccountMessage;
    @Override
    protected Task<UserAccountMessage> createTask() {
        return new Task<UserAccountMessage>() {
            @Override
            protected UserAccountMessage call() throws Exception {
                SmartCardCommunication communication = SmartCardCommunication.getInstance();
                communication.getUserAccountDataFromCard(userAccountMessage);
                return userAccountMessage;
            }
        };
    }
    public void setUserAccountMessage(UserAccountMessage userAccountMessage) {
        this.userAccountMessage = userAccountMessage;
    }
    public UserAccountMessage getUserAccountMessage() {
        return userAccountMessage;
    }
}















