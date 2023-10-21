package com.food.delivery.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.food.delivery.domain.enumeration.Tag;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TagModel.
 */
@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TagModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private Tag name;

    @ManyToMany(mappedBy = "tags")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "restaurant", "ingredients", "tags" }, allowSetters = true)
    private Set<MenuModel> menus = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TagModel id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tag getName() {
        return this.name;
    }

    public TagModel name(Tag name) {
        this.setName(name);
        return this;
    }

    public void setName(Tag name) {
        this.name = name;
    }

    public Set<MenuModel> getMenus() {
        return this.menus;
    }

    public void setMenus(Set<MenuModel> menus) {
        if (this.menus != null) {
            this.menus.forEach(i -> i.removeTag(this));
        }
        if (menus != null) {
            menus.forEach(i -> i.addTag(this));
        }
        this.menus = menus;
    }

    public TagModel menus(Set<MenuModel> menus) {
        this.setMenus(menus);
        return this;
    }

    public TagModel addMenu(MenuModel menu) {
        this.menus.add(menu);
        menu.getTags().add(this);
        return this;
    }

    public TagModel removeMenu(MenuModel menu) {
        this.menus.remove(menu);
        menu.getTags().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagModel)) {
            return false;
        }
        return id != null && id.equals(((TagModel) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagModel{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
