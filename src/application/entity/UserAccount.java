package application.entity;

import com.google.gson.annotations.Expose;

public class UserAccount {
    @Expose
    private String username;
    @Expose
    private String passwordToStore;
    @Expose
    private String password;
    private boolean savedToCard;

    public UserAccount(String username, String password, boolean savedToCard, String passwordToStore) {
        this.username = username;
        this.password = password;
        this.savedToCard = savedToCard;
        this.passwordToStore = passwordToStore;
    }

    public String getPasswordToStore() {
        return passwordToStore;
    }

    public void setPasswordToStore(String passwordToStore) {
        this.passwordToStore = passwordToStore;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSavedToCard() {
        return savedToCard;
    }

    public void setSavedToCard(boolean savedToCard) {
        this.savedToCard = savedToCard;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", savedToCard=" + savedToCard +
                '}';
    }
}
