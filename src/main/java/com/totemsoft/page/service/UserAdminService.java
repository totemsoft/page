package com.totemsoft.page.service;

import java.time.Instant;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.RoleEnum;
import com.totemsoft.page.model.SearchData;
import com.totemsoft.page.model.UserDto;
import com.totemsoft.page.model.entity.User;
import com.totemsoft.page.model.mapper.UserMapper;
import com.totemsoft.page.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@PreAuthorize(RoleEnum.HAS_ROLE_ADMIN_USER)
@Transactional
@RequiredArgsConstructor
@Log4j2
public class UserAdminService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    // no @PreAuthorize as it's called from UserService OidcUserService#loadUser
    @PreAuthorize(RoleEnum.PERMIT_ALL)
    public UserDto findUser(OidcUser oidcUser) {
        return userMapper.map(getUser(oidcUser));
    }

    private User getUser(OidcUser oidcUser) {
        final var email = oidcUser.getEmail();
        if (userRepository.existsById(email)) {
            log.debug("Found User: {}", email);
            return userRepository.findById(email)
                .orElseThrow(() -> new EntityNotFoundException(email, User.class));
        }
        log.debug("Saving User: {}", email);
        return userRepository.save(User.builder()
            .email(email)
            .name(oidcUser.getName())
            .givenName(oidcUser.getGivenName())
            .familyName(oidcUser.getFamilyName())
            .middleName(oidcUser.getMiddleName())
            .gender(oidcUser.getGender())
            .birthdate(StringUtils.isBlank(oidcUser.getBirthdate()) ? null : LocalDate.parse(oidcUser.getBirthdate()))
            .updatedAt(oidcUser.getUpdatedAt() == null ? Instant.now() : oidcUser.getUpdatedAt())
            .build());
    }

    public SearchData<UserDto> findUsers() {
        final var users = userRepository.findAll(Sort.by("email"));
        return SearchData.<UserDto>builder()
            .records(userMapper.mapUsers(users))
            .build();
    }

    public void saveUserAuthorities(UserDto userDto) {
        final var email = userDto.getEmail();
        final var entity = userRepository.findById(email)
            .orElseThrow(() -> new EntityNotFoundException(email, User.class));
        entity.setAuthorities(userDto.getAuthorities());
        userRepository.save(entity);
        log.debug("Saved User Authorities: {}", entity);
    }

}
