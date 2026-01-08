package com.totemsoft.page.model.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.entity.Tag;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PageMapper {

    PageDto map(Page page);

    TagDto map(Tag tag);

    Set<TagDto> mapTags(Set<Tag> tags);

}
