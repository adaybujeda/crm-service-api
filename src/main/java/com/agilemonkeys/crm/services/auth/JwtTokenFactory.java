package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.config.CrmJwtConfig;
import com.agilemonkeys.crm.domain.CrmAuthToken;
import com.agilemonkeys.crm.domain.UserRole;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

public class JwtTokenFactory {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenFactory.class);

    private final Signer jwtTokenSigner;
    private final long tokenTTLInSeconds;

    public JwtTokenFactory(CrmJwtConfig config) {
        jwtTokenSigner = HMACSigner.newSHA256Signer(config.getSecret());
        tokenTTLInSeconds = config.getTokenTimeToLive().toSeconds();
    }

    public CrmAuthToken createToken(UUID userId, UserRole role) {
        JWT jwt = new JWT().setIssuer(CrmJwtConfig.ISSUER)
                .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
                .setSubject(userId.toString())
                .addClaim(CrmJwtConfig.CLAIM_ROLE, role)
                .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(tokenTTLInSeconds));

        String encodedJwtToken = JWT.getEncoder().encode(jwt, jwtTokenSigner);
        CrmAuthToken crmAuthToken = new CrmAuthToken(userId, tokenTTLInSeconds, encodedJwtToken);
        log.info("action=createJwtToken result=success userId={} role={} crmAuthToken={}", userId, role, crmAuthToken);
        return crmAuthToken;
    }
}
