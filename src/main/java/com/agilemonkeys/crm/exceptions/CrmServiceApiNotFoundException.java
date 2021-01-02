package com.agilemonkeys.crm.exceptions;

import org.eclipse.jetty.http.HttpStatus;

public class CrmServiceApiNotFoundException extends CrmServiceApiException {
    public CrmServiceApiNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND_404, message);
    }
}
