package application.services;

import application.SmartCardCommunication;
import entity.LoginMessage;
import entity.StatusCode;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LoginService extends Service<LoginMessage> {

    private LoginMessage loginMessage;

    public LoginService() {

    }

    public LoginMessage getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(LoginMessage loginMessage) {
        this.loginMessage = loginMessage;
    }

    @Override
    protected Task<LoginMessage> createTask() {
        return new Task<LoginMessage>() {
            @Override
            protected LoginMessage call() throws Exception {

                SmartCardCommunication communication = SmartCardCommunication.getInstance();
                loginMessage.setStatusCode(StatusCode.OK);

                communication.setCardTerminal(loginMessage.getCardReader());
                communication.establishSecureChannel();
                communication.verifyPin(loginMessage.getUserPin());
                

                return loginMessage;
            }
        };
    }
}