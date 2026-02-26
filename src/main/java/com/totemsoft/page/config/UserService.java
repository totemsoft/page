package com.totemsoft.page.config;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.totemsoft.page.model.entity.User;
import com.totemsoft.page.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
class UserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        final var user = super.loadUser(userRequest);
        log.debug("Loaded User [{}]: {}", user.getClass().getCanonicalName(), user);
        final var entity = userRepository.save(User.builder()
            .email(user.getEmail())
            .name(user.getName())
            .givenName(user.getGivenName())
            .familyName(user.getFamilyName())
            .middleName(user.getMiddleName())
            .gender(user.getGender())
            .birthdate(StringUtils.isBlank(user.getBirthdate()) ? null : LocalDate.parse(user.getBirthdate()))
            .updatedAt(user.getUpdatedAt())
            .build());
        log.debug("Saved User: {}", entity);
        return user;
    }

}
