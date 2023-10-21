package com.food.delivery.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MenuModel.
 */
@Entity
@Table(name = "menu")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MenuModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "price", nullable = false)
    private Long price;

    @ManyToOne
    private RestaurantModel restaurant;

    @ManyToMany
    @JoinTable(
        name = "rel_menu__ingredient",
        joinColumns = @JoinColumn(name = "menu_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "menus" }, allowSetters = true)
    private Set<IngredientModel> ingredients = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "rel_menu__tag", joinColumns = @JoinColumn(name = "menu_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "menus" }, allowSetters = true)
    private Set<TagModel> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MenuModel id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MenuModel name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public MenuModel description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return this.price;
    }

    public MenuModel price(Long price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public RestaurantModel getRestaurant() {
        return this.restaurant;
    }

    public void setRestaurant(RestaurantModel restaurant) {
        this.restaurant = restaurant;
    }

    public MenuModel restaurant(RestaurantModel restaurant) {
        this.setRestaurant(restaurant);
        return this;
    }

    public Set<IngredientModel> getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(Set<IngredientModel> ingredients) {
        this.ingredients = ingredients;
    }

    public MenuModel ingredients(Set<IngredientModel> ingredients) {
        this.setIngredients(ingredients);
        return this;
    }

    public MenuModel addIngredient(IngredientModel ingredient) {
        this.ingredients.add(ingredient);
        ingredient.getMenus().add(this);
        return this;
    }

    public MenuModel removeIngredient(IngredientModel ingredient) {
        this.ingredients.remove(ingredient);
        ingredient.getMenus().remove(this);
        return this;
    }

    public Set<TagModel> getTags() {
        return this.tags;
    }

    public void setTags(Set<TagModel> tags) {
        this.tags = tags;
    }

    public MenuModel tags(Set<TagModel> tags) {
        this.setTags(tags);
        return this;
    }

    public MenuModel addTag(TagModel tag) {
        this.tags.add(tag);
        tag.getMenus().add(this);
        return this;
    }

    public MenuModel removeTag(TagModel tag) {
        this.tags.remove(tag);
        tag.getMenus().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuModel)) {
            return false;
        }
        return id != null && id.equals(((MenuModel) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuModel{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
