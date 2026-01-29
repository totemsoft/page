package com.totemsoft.page.config;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
class UserService extends OidcUserService {@Override

    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        final var user = super.loadUser(userRequest);
        log.debug("Loaded User [{}]: {}", user.getClass().getCanonicalName(), user);
        return user;
    }

}
