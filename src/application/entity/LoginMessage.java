package application.entity;

public class LoginMessage {
    protected String masterPin;
    protected String userPin;
    protected String response;
    protected int numberOfAttemptsLeft;
    protected String cardReader;
    protected int statusCode;

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getCardReader() {
        return this.cardReader;
    }

    public void setCardReader(String cardReader) {
        this.cardReader = cardReader;
    }

    public String getMasterPin() {
        return this.masterPin;
    }

    public void setMasterPin(String masterPin) {
        this.masterPin = masterPin;
    }

    public String getUserPin() {
        return this.userPin;
    }

    public void setUserPin(String userPin) {
        this.userPin = userPin;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getNumberOfAttemptsLeft() {
        return this.numberOfAttemptsLeft;
    }

    public void setNumberOfAttemptsLeft(int numberOfAttemptsLeft) {
        this.numberOfAttemptsLeft = numberOfAttemptsLeft;
    }
}
