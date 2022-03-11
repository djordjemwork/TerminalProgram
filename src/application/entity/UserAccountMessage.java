package application.entity;

public class UserAccountMessage {
    private String userAccountListJSON;
    private String response;
    private String cardReader;
    private int statusCode;

    public String getUserAccountList() {
        return userAccountListJSON;
    }

    public void setUserAccountList(String userAccountListJSON) {
        this.userAccountListJSON = userAccountListJSON;
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
