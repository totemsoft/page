package com.totemsoft.page.config;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.totemsoft.page.model.entity.User;
import com.totemsoft.page.repository.UserRepository;
import com.totemsoft.page.service.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
class UserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        final var user = super.loadUser(userRequest);
        log.debug("Loaded User [{}]: {}", user.getClass().getCanonicalName(), user);
        final var entity = findUser(user);
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

    User findUser(OidcUser user) {
        final var email = user.getEmail();
        if (userRepository.existsById(email)) {
            log.debug("Found User: {}", email);
            return userRepository.findById(email)
                .orElseThrow(() -> new EntityNotFoundException(email, User.class));
        }
        log.debug("Saving User: {}", email);
        return userRepository.save(User.builder()
            .email(email)
            .name(user.getName())
            .givenName(user.getGivenName())
            .familyName(user.getFamilyName())
            .middleName(user.getMiddleName())
            .gender(user.getGender())
            .birthdate(StringUtils.isBlank(user.getBirthdate()) ? null : LocalDate.parse(user.getBirthdate()))
            .updatedAt(user.getUpdatedAt() == null ? Instant.now() : user.getUpdatedAt())
            .build());
    }

}
