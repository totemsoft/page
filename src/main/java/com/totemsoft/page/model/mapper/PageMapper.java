package com.totemsoft.page.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.CurrencyDto;
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
import com.totemsoft.page.model.entity.exchangerates.Currency;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PageMapper {

    List<CurrencyDto> map(Collection<Currency> currencies);

    List<KeyDto> mapKeys(Collection<Key> keys);
    List<Key> mapKeyDtos(List<KeyDto> keyDtos);

    PageDto map(Page page);
    Page map(PageDto pageDto);
    List<PageDto> map(List<Page> pages);

    Tab map(TabDto tab);

    TagDto map(Tag tag);
    List<TagDto> mapTags(List<Tag> tags);
    Tag map(TagDto tagDto);

    TagTypeDto map(TagType tagType);
    TagType map(TagTypeDto tagTypeDto);
    List<TagTypeDto> mapTagTypes(List<TagType> tagTypes);

    Section map(SectionDto sectionDto);

    SubSection map(SubSectionDto subSectionDto);

}
