package com.totemsoft.page.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.entity.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PageMapper {

    PageDto map(Page page);

}
