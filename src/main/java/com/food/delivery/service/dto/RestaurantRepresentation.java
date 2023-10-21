package com.food.delivery.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.food.delivery.domain.RestaurantModel} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RestaurantRepresentation implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private String adress;

    private Integer rating;

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

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantRepresentation)) {
            return false;
        }

        RestaurantRepresentation restaurantRepresentation = (RestaurantRepresentation) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, restaurantRepresentation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantRepresentation{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", adress='" + getAdress() + "'" +
            ", rating=" + getRating() +
            "}";
    }
}
