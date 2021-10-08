package application.services;

import application.SmartCardCommunication;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import entity.StatusCode;
import entity.UserAccountMessage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import com.google.gson.Gson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Array;
import java.util.Arrays;

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
                userAccountMessage.setStatusCode(StatusCode.OK);
                communication.putUserAccountDataToCard(userAccountMessage.getUserAccountList());

                return userAccountMessage;
            }
        };
    }
}
