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
        final var oidcUser = super.loadUser(userRequest);
        log.debug("Loaded User [{}]: {}", oidcUser.getClass().getCanonicalName(), oidcUser);
        final var user = userAdminService.findUser(oidcUser);
        log.debug("User: {}", user);
        if (!user.getAuthorities().isEmpty()) {
            final var authorities = new ArrayList<GrantedAuthority>(oidcUser.getAuthorities());
            user.getAuthorities().forEach(a ->
                authorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_PREFIX + a)));
            log.trace("Enhanced User authorities: {}", authorities);
            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        }
        return oidcUser;
    }

}
