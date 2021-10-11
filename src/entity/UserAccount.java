package entity;

public class UserAccount {
    private String username;
    private String password;
    private boolean savedToCard;

    public UserAccount(String username, String password, boolean savedToCard) {
        this.username = username;
        this.password = password;
        this.savedToCard = savedToCard;
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

    public boolean isSavedToCard() {
        return savedToCard;
    }

    public void setSavedToCard(boolean savedToCard) {
        this.savedToCard = savedToCard;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
