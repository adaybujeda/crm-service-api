package com.agilemonkeys.crm.exceptions;

import org.eclipse.jetty.http.HttpStatus;

public class CrmServiceApiDeletedException extends CrmServiceApiException {
    public CrmServiceApiDeletedException(String message) {
        super(HttpStatus.METHOD_NOT_ALLOWED_405, message);
    }
}
