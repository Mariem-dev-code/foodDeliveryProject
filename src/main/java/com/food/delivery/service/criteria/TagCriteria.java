package com.food.delivery.service.criteria;

import com.food.delivery.domain.enumeration.Tag;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.food.delivery.domain.TagModel} entity. This class is used
 * in {@link com.food.delivery.web.rest.TagResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tags?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TagCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Tag
     */
    public static class TagFilter extends Filter<Tag> {

        public TagFilter() {}

        public TagFilter(TagFilter filter) {
            super(filter);
        }

        @Override
        public TagFilter copy() {
            return new TagFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private TagFilter name;

    private LongFilter menuId;

    private Boolean distinct;

    public TagCriteria() {}

    public TagCriteria(TagCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.menuId = other.menuId == null ? null : other.menuId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TagCriteria copy() {
        return new TagCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public TagFilter getName() {
        return name;
    }

    public TagFilter name() {
        if (name == null) {
            name = new TagFilter();
        }
        return name;
    }

    public void setName(TagFilter name) {
        this.name = name;
    }

    public LongFilter getMenuId() {
        return menuId;
    }

    public LongFilter menuId() {
        if (menuId == null) {
            menuId = new LongFilter();
        }
        return menuId;
    }

    public void setMenuId(LongFilter menuId) {
        this.menuId = menuId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TagCriteria that = (TagCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(menuId, that.menuId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, menuId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (menuId != null ? "menuId=" + menuId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
