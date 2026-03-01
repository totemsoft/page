package com.totemsoft.page.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.totemsoft.page.model.SearchResult;
import com.totemsoft.page.model.UserDto;
import com.totemsoft.page.service.UserAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class UserController {

    private final UserAdminService userAdminService;

    @GetMapping("/users/user")
    SearchResult<UserDto> findUsers() {
        return SearchResult.<UserDto>builder()
            .columns(UserDto.columns())
            .data(userAdminService.findUsers())
            .build();
    }

    @PostMapping("/users/user")
    void saveUserAuthorities(@RequestBody UserDto userDto) {
        userAdminService.saveUserAuthorities(userDto);
    }

}
