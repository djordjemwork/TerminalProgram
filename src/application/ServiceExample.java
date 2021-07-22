package application;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceExample extends Service<String> {

    private String pinString;

    public ServiceExample() {

    }

    public String getPinString() {
        return pinString;
    }

    public void setPinString(String pinString) {
        this.pinString = pinString;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                //DO YOU HARD STUFF HERE
                SmartCardCommunication communication = SmartCardCommunication.getInstance();

                if (!communication.establishSecureChannel(CardReader.cardReader)) {
                    //System.out.println("Error in establishing secure channel");
                    return "error";
                }
                if (!communication.verifyPin(pinString)) {
                    //System.out.println("Wrong pin, try again");
                    return "error";
                }
                return "OK";
            }
        };
    }
}