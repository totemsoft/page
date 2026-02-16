package com.totemsoft.page.model.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.totemsoft.page.model.ColumnDef.DropdownOption;
import com.totemsoft.page.model.entity.TagType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DropdownOptionMapper {

    @Mapping(target = "value", source = "id")
    @Mapping(target = "label", source = "name")
    DropdownOption mapTagType(TagType tagType);
    List<DropdownOption> mapTagType(Collection<TagType> tagTypes);

}
