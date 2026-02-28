package com.totemsoft.page.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.UserDto;
import com.totemsoft.page.model.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User map(UserDto userDto);
    UserDto map(User user);
    List<UserDto> mapUsers(List<User> users);

}
