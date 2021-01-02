package com.agilemonkeys.crm.exceptions;

import org.eclipse.jetty.http.HttpStatus;

public class CrmServiceApiStaleStateException extends CrmServiceApiException {
    public CrmServiceApiStaleStateException(String message) {
        super(HttpStatus.PRECONDITION_FAILED_412, message);
    }
}
