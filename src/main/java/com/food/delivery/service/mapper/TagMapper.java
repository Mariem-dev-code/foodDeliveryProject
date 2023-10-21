package com.food.delivery.service.mapper;

import com.food.delivery.domain.TagModel;
import com.food.delivery.service.dto.TagRepresentation;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TagModel} and its DTO {@link TagRepresentation}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagRepresentation, TagModel> {}
