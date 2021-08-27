package application.exceptions;


import entity.StatusCode;

import net.sf.scuba.smartcards.CardServiceException;

public class CardInitException extends CardServiceException {
    /**
     *
     */
    private static final long serialVersionUID = -3528218704635439430L;
    private int statusCode;

    public CardInitException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public CardInitException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}