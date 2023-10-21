package com.food.delivery.service.dto;

import com.food.delivery.domain.enumeration.Tag;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.food.delivery.domain.TagModel} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TagRepresentation implements Serializable {

    private Long id;

    private Tag name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tag getName() {
        return name;
    }

    public void setName(Tag name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagRepresentation)) {
            return false;
        }

        TagRepresentation tagRepresentation = (TagRepresentation) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tagRepresentation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagRepresentation{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
