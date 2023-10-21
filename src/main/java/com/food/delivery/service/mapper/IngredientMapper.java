package com.food.delivery.service.mapper;

import com.food.delivery.domain.IngredientModel;
import com.food.delivery.service.dto.IngredientRepresentation;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link IngredientModel} and its DTO {@link IngredientRepresentation}.
 */
@Mapper(componentModel = "spring")
public interface IngredientMapper extends EntityMapper<IngredientRepresentation, IngredientModel> {}
