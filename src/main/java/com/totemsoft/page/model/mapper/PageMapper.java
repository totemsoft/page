package com.totemsoft.page.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.SectionDto;
import com.totemsoft.page.model.SubSectionDto;
import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.entity.Section;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.entity.Tab;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PageMapper {

    List<KeyDto> mapKeys(List<Key> keys);
    List<Key> mapKeyDtos(List<KeyDto> keys);

    PageDto map(Page page);
    Page map(PageDto pageDto);

    Tab map(TabDto tab);

    TagDto map(Tag tag);
    List<TagDto> mapTags(List<Tag> tags);
    @Mapping(target = "tagType", ignore = true)
    Tag map(TagDto tagDto);

    TagTypeDto map(TagType tagType);
    List<TagTypeDto> mapTagTypes(List<TagType> tagTypes);

    Section map(SectionDto sectionDto);

    @Mapping(target = "rowTagType", ignore = true)
    @Mapping(target = "columnTagType", ignore = true)
    SubSection map(SubSectionDto subSectionDto);

}
