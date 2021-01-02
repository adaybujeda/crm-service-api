package com.agilemonkeys.crm.exceptions;

public abstract class CrmServiceApiException extends RuntimeException {

    private int httpStatusCode;

    public CrmServiceApiException(int httpStatusCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
