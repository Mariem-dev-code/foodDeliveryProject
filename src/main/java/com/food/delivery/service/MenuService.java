package com.food.delivery.service;

import com.food.delivery.service.dto.MenuRepresentation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.food.delivery.domain.MenuModel}.
 */
public interface MenuService {
    /**
     * Save a menu.
     *
     * @param menuRepresentation the entity to save.
     * @return the persisted entity.
     */
    MenuRepresentation save(MenuRepresentation menuRepresentation);

    /**
     * Updates a menu.
     *
     * @param menuRepresentation the entity to update.
     * @return the persisted entity.
     */
    MenuRepresentation update(MenuRepresentation menuRepresentation);

    /**
     * Partially updates a menu.
     *
     * @param menuRepresentation the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MenuRepresentation> partialUpdate(MenuRepresentation menuRepresentation);

    /**
     * Get all the menus.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MenuRepresentation> findAll(Pageable pageable);

    /**
     * Get all the menus with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MenuRepresentation> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" menu.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MenuRepresentation> findOne(Long id);

    /**
     * Delete the "id" menu.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
