package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.config.CrmJwtConfig;
import com.agilemonkeys.crm.domain.AuthenticatedUser;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.exceptions.CrmServiceApiAuthException;
import io.fusionauth.jwt.InvalidJWTException;
import io.fusionauth.jwt.InvalidJWTSignatureException;
import io.fusionauth.jwt.JWTExpiredException;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class JwtTokenVerifier {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenVerifier.class);

    private final Verifier jwtTokenVerifier;

    public JwtTokenVerifier(CrmJwtConfig config) {
        jwtTokenVerifier = HMACVerifier.newVerifier(config.getSecret());
    }

    public AuthenticatedUser verifyToken(String encodedJwtToken) {
        try {
            JWT decodedJwt = JWT.getDecoder().decode(encodedJwtToken, jwtTokenVerifier);
            UUID userId = UUID.fromString(decodedJwt.subject);
            UserRole role = UserRole.valueOf((String)decodedJwt.getOtherClaims().get(CrmJwtConfig.CLAIM_ROLE));
            AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, role);
            log.info("action=verifyToken result=success authPrincipal={}", authenticatedUser);
            return authenticatedUser;
        } catch (InvalidJWTException invalidException) {
            throw new CrmServiceApiAuthException("Invalid auth token");
        } catch (InvalidJWTSignatureException signatureException) {
            throw new CrmServiceApiAuthException("Invalid signature in auth token");
        } catch (JWTExpiredException expiredException) {
            throw new CrmServiceApiAuthException("Expired auth token");
        }
    }
}
