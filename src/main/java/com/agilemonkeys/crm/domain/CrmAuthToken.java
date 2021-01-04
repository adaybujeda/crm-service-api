package com.agilemonkeys.crm.domain;

import com.google.common.base.MoreObjects;

import java.util.UUID;

public class CrmAuthToken {

    private final UUID userId;
    private final long tokenTTLInSeconds;
    private final String encodedJwtToken;

    public CrmAuthToken(UUID userId, long tokenTTLInSeconds, String encodedJwtToken) {
        this.userId = userId;
        this.tokenTTLInSeconds = tokenTTLInSeconds;
        this.encodedJwtToken = encodedJwtToken;
    }

    public UUID getUserId() {
        return userId;
    }

    public long getTokenTTLInSeconds() {
        return tokenTTLInSeconds;
    }

    public String getEncodedJwtToken() {
        return encodedJwtToken;
    }

    private String getLastCharacters(int numberOfCharacters) {
        if(encodedJwtToken == null || encodedJwtToken.length() <= numberOfCharacters) {
            return encodedJwtToken;
        }
        return String.format("...%s", encodedJwtToken.substring(encodedJwtToken.length() - numberOfCharacters));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("tokenTTLInSeconds", tokenTTLInSeconds)
                .add("jwtEncodedToken", getLastCharacters(10))
                .toString();
    }

}
