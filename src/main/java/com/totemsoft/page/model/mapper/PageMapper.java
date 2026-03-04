package com.totemsoft.page.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.SectionDto;
import com.totemsoft.page.model.SubSectionDto;
import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.model.entity.Page;
import com.totemsoft.page.model.entity.Section;
import com.totemsoft.page.model.entity.SubSection;
import com.totemsoft.page.model.entity.Tab;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PageMapper {

    PageDto map(Page page);
    Page map(PageDto pageDto);
    List<PageDto> map(List<Page> pages);

    Tab mapTab(TabDto tab);

    Section mapSection(SectionDto sectionDto);

    SubSection mapSubSection(SubSectionDto subSectionDto);

}
