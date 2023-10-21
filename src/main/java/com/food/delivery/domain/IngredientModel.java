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
 * A IngredientModel.
 */
@Entity
@Table(name = "ingredient")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IngredientModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "ingredients")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "restaurant", "ingredients", "tags" }, allowSetters = true)
    private Set<MenuModel> menus = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IngredientModel id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public IngredientModel name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MenuModel> getMenus() {
        return this.menus;
    }

    public void setMenus(Set<MenuModel> menus) {
        if (this.menus != null) {
            this.menus.forEach(i -> i.removeIngredient(this));
        }
        if (menus != null) {
            menus.forEach(i -> i.addIngredient(this));
        }
        this.menus = menus;
    }

    public IngredientModel menus(Set<MenuModel> menus) {
        this.setMenus(menus);
        return this;
    }

    public IngredientModel addMenu(MenuModel menu) {
        this.menus.add(menu);
        menu.getIngredients().add(this);
        return this;
    }

    public IngredientModel removeMenu(MenuModel menu) {
        this.menus.remove(menu);
        menu.getIngredients().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IngredientModel)) {
            return false;
        }
        return id != null && id.equals(((IngredientModel) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IngredientModel{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
