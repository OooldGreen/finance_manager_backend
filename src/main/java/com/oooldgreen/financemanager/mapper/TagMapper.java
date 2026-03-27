package com.oooldgreen.financemanager.mapper;

import com.oooldgreen.financemanager.dto.TagDTO;
import com.oooldgreen.financemanager.entity.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDTO toDTO(Tag tag);
    List<TagDTO> toDTOs(List<Tag> tags);
}
