package com.agilemonkeys.crm.exceptions;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class ErrorResponse {

    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ErrorResponse other = (ErrorResponse) obj;
        return Objects.equals(this.message, other.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.message);

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("message", message)
                .toString();
    }
}
