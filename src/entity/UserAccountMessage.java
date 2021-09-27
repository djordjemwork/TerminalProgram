package entity;

import java.util.List;

public class UserAccountMessage {
    private List<UserAccount> userAccountList;
    private String response;
    private String cardReader;
    private int statusCode;

    public List<UserAccount> getUserAccountList() {
        return userAccountList;
    }

    public void setUserAccountList(List<UserAccount> userAccountList) {
        this.userAccountList = userAccountList;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCardReader() {
        return cardReader;
    }

    public void setCardReader(String cardReader) {
        this.cardReader = cardReader;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
