package com.food.delivery.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.food.delivery.domain.IngredientModel} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IngredientRepresentation implements Serializable {

    private Long id;

    @NotNull
    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IngredientRepresentation)) {
            return false;
        }

        IngredientRepresentation ingredientRepresentation = (IngredientRepresentation) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ingredientRepresentation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IngredientRepresentation{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
