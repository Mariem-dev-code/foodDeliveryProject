package com.food.delivery.service.mapper;

import com.food.delivery.domain.IngredientModel;
import com.food.delivery.domain.MenuModel;
import com.food.delivery.domain.RestaurantModel;
import com.food.delivery.domain.TagModel;
import com.food.delivery.service.dto.IngredientRepresentation;
import com.food.delivery.service.dto.MenuRepresentation;
import com.food.delivery.service.dto.RestaurantRepresentation;
import com.food.delivery.service.dto.TagRepresentation;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MenuModel} and its DTO {@link MenuRepresentation}.
 */
@Mapper(componentModel = "spring")
public interface MenuMapper extends EntityMapper<MenuRepresentation, MenuModel> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    @Mapping(target = "ingredients", source = "ingredients", qualifiedByName = "ingredientIdSet")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagIdSet")
    MenuRepresentation toDto(MenuModel s);

    @Mapping(target = "removeIngredient", ignore = true)
    @Mapping(target = "removeTag", ignore = true)
    MenuModel toEntity(MenuRepresentation menuRepresentation);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantRepresentation toDtoRestaurantId(RestaurantModel restaurantModel);

    @Named("ingredientId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    IngredientRepresentation toDtoIngredientId(IngredientModel ingredientModel);

    @Named("ingredientIdSet")
    default Set<IngredientRepresentation> toDtoIngredientIdSet(Set<IngredientModel> ingredientModel) {
        return ingredientModel.stream().map(this::toDtoIngredientId).collect(Collectors.toSet());
    }

    @Named("tagId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TagRepresentation toDtoTagId(TagModel tagModel);

    @Named("tagIdSet")
    default Set<TagRepresentation> toDtoTagIdSet(Set<TagModel> tagModel) {
        return tagModel.stream().map(this::toDtoTagId).collect(Collectors.toSet());
    }
}
