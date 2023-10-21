package com.food.delivery.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.food.delivery.domain.MenuModel} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuRepresentation implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private Long price;

    private RestaurantRepresentation restaurant;

    private Set<IngredientRepresentation> ingredients = new HashSet<>();

    private Set<TagRepresentation> tags = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public RestaurantRepresentation getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantRepresentation restaurant) {
        this.restaurant = restaurant;
    }

    public Set<IngredientRepresentation> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<IngredientRepresentation> ingredients) {
        this.ingredients = ingredients;
    }

    public Set<TagRepresentation> getTags() {
        return tags;
    }

    public void setTags(Set<TagRepresentation> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuRepresentation)) {
            return false;
        }

        MenuRepresentation menuRepresentation = (MenuRepresentation) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, menuRepresentation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuRepresentation{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", restaurant=" + getRestaurant() +
            ", ingredients=" + getIngredients() +
            ", tags=" + getTags() +
            "}";
    }
}
