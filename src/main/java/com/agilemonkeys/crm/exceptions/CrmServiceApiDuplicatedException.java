package com.agilemonkeys.crm.exceptions;

import org.eclipse.jetty.http.HttpStatus;

public class CrmServiceApiDuplicatedException extends CrmServiceApiException {
    public CrmServiceApiDuplicatedException(String message) {
        super(HttpStatus.CONFLICT_409, message);
    }
}
