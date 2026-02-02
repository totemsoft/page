package com.totemsoft.page.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.entity.Key;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SetupMapper {

    Key map(KeyDto keyDto);
    KeyDto map(Key key);
    List<KeyDto> map(List<Key> keys);

}
