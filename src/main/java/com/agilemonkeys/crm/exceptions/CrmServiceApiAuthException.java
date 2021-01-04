package com.agilemonkeys.crm.exceptions;

import org.eclipse.jetty.http.HttpStatus;

public class CrmServiceApiAuthException extends CrmServiceApiException {
    public CrmServiceApiAuthException(String message) {
        super(HttpStatus.UNAUTHORIZED_401, message);
    }
}
