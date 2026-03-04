package com.totemsoft.page.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SetupMapper {

    //@Mapping(target = "tags.tagType", ignore = true)
    Key mapKey(KeyDto keyDto);
    KeyDto mapKey(Key key);
    List<KeyDto> mapKey(Collection<Key> keys);
    List<Key> mapKeyDto(Collection<KeyDto> keyDtos);

    TagDto mapTag(Tag tag);
    List<TagDto> mapTag(Collection<Tag> tags);
    @Mapping(target = "tagType", ignore = true)
    Tag mapTag(TagDto tagDto);

    TagTypeDto mapTagType(TagType tagType);
    TagType mapTagType(TagTypeDto tagTypeDto);
    List<TagTypeDto> mapTagType(Collection<TagType> tagTypes);

}
