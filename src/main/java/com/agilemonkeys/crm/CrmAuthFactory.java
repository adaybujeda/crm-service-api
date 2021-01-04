package com.agilemonkeys.crm;

import com.agilemonkeys.crm.config.CrmServiceApiConfiguration;
import com.agilemonkeys.crm.domain.AuthenticatedUser;
import com.agilemonkeys.crm.services.auth.CrmJwtRequestAuthenticator;
import com.agilemonkeys.crm.services.auth.CrmRoleBasedRequestAuthorizer;
import com.agilemonkeys.crm.services.auth.JwtTokenFactory;
import com.agilemonkeys.crm.services.auth.JwtTokenVerifier;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrmAuthFactory {

    private static final Logger log = LoggerFactory.getLogger(CrmAuthFactory.class);

    public CrmAuthContext init(final CrmServiceApiConfiguration config, final Environment environment) {
        JwtTokenFactory jwtTokenFactory = new JwtTokenFactory(config.getJwtConfig());
        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(config.getJwtConfig());

        CrmJwtRequestAuthenticator authenticator = new CrmJwtRequestAuthenticator(jwtTokenVerifier);
        CrmRoleBasedRequestAuthorizer authorizer = new CrmRoleBasedRequestAuthorizer();

        environment.jersey().register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<AuthenticatedUser>()
                        .setAuthenticator(authenticator)
                        .setAuthorizer(authorizer)
                        .setPrefix("Bearer")
                        .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        //If you want to use @Auth to inject a custom Principal type into your resource
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthenticatedUser.class));

        log.info("action=auth-init completed");
        return new CrmAuthContext(jwtTokenFactory, jwtTokenVerifier);
    }

    public static final class CrmAuthContext {
        JwtTokenFactory jwtTokenFactory;
        JwtTokenVerifier jwtTokenVerifier;

        public CrmAuthContext(JwtTokenFactory jwtTokenFactory, JwtTokenVerifier jwtTokenVerifier) {
            this.jwtTokenFactory = jwtTokenFactory;
            this.jwtTokenVerifier = jwtTokenVerifier;
        }

        public JwtTokenFactory getJwtTokenFactory() {
            return jwtTokenFactory;
        }

        public JwtTokenVerifier getJwtTokenVerifier() {
            return jwtTokenVerifier;
        }
    }
}
