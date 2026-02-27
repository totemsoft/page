package com.totemsoft.page.service;

import java.time.Instant;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.totemsoft.page.model.entity.User;
import com.totemsoft.page.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
//@PreAuthorize(SecurityConfig.HAS_ROLE_ADMIN_USER)
@RequiredArgsConstructor
@Log4j2
public class UserAdminService {

    private final UserRepository userRepository;

    // no @PreAuthorize as it's called from UserService OidcUserService#loadUser
    @Transactional
    public User findUser(OidcUser user) {
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
