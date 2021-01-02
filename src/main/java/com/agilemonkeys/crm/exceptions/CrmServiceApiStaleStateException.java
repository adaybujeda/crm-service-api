package com.agilemonkeys.crm.exceptions;

public class CrmServiceApiStaleStateException extends RuntimeException {
    public CrmServiceApiStaleStateException(String message) {
        super(message);
    }
}
