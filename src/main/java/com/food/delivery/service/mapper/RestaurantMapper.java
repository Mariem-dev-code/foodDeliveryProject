package com.food.delivery.service.mapper;

import com.food.delivery.domain.RestaurantModel;
import com.food.delivery.service.dto.RestaurantRepresentation;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RestaurantModel} and its DTO {@link RestaurantRepresentation}.
 */
@Mapper(componentModel = "spring")
public interface RestaurantMapper extends EntityMapper<RestaurantRepresentation, RestaurantModel> {}
