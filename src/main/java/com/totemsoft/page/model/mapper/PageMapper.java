package com.totemsoft.page.model.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
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

    List<KeyDto> mapKeys(Set<Key> keys);

    PageDto map(Page page);
    Page map(PageDto pageDto);

    Tab map(TabDto tab);

    TagDto map(Tag tag);
    List<TagDto> mapTags(List<Tag> tags);

    TagTypeDto map(TagType tagType);
    List<TagTypeDto> mapTagTypes(List<TagType> tagTypes);

    Section map(SectionDto sectionDto);

    // WARN: Unmapped target properties: "rowTagType, columnTagType, keys"
    SubSection map(SubSectionDto subSectionDto);

}
