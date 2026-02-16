package com.totemsoft.page.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.exchangerates.v1.model.CurrencyDto;
import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.SectionDto;
import com.totemsoft.page.model.SubSectionDto;
import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.entity.Section;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.entity.Tab;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;
import com.totemsoft.page.model.entity.exchangerates.Currency;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PageMapper {

    List<CurrencyDto> mapCurrency(Collection<Currency> currencies);

    PageDto map(Page page);
    Page map(PageDto pageDto);
    List<PageDto> map(List<Page> pages);

    Tab mapTab(TabDto tab);

    TagDto mapTag(Tag tag);
    List<TagDto> mapTag(Collection<Tag> tags);
    Tag mapTag(TagDto tagDto);

    TagTypeDto mapTagType(TagType tagType);
    TagType mapTagType(TagTypeDto tagTypeDto);
    List<TagTypeDto> mapTagType(Collection<TagType> tagTypes);

    Section mapSection(SectionDto sectionDto);

    SubSection mapSubSection(SubSectionDto subSectionDto);

}
