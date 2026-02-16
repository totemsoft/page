package com.totemsoft.page.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.entity.Key;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SetupMapper {

    Key mapKey(KeyDto keyDto);
    KeyDto mapKey(Key key);
    List<KeyDto> mapKey(Collection<Key> keys);
    List<Key> mapKeyDto(Collection<KeyDto> keyDtos);

}
