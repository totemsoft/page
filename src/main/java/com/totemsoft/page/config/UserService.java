package com.totemsoft.page.config;

import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.totemsoft.page.service.UserAdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
class UserService extends OidcUserService {

    private final UserAdminService userAdminService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        final var user = super.loadUser(userRequest);
        log.debug("Loaded User [{}]: {}", user.getClass().getCanonicalName(), user);
        final var entity = userAdminService.findUser(user);
        log.debug("User: {}", entity);
        if (!entity.getAuthorities().isEmpty()) {
            final var authorities = new ArrayList<GrantedAuthority>(user.getAuthorities());
            entity.getAuthorities().forEach(a ->
                authorities.add(new SimpleGrantedAuthority(SecurityConfig.ROLE_PREFIX + a)));
            log.trace("Enhanced User authorities: {}", authorities);
            return new DefaultOidcUser(authorities, user.getIdToken(), user.getUserInfo());
        }
        return user;
    }

}
