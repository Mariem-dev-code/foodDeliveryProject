package com.food.delivery.service;

import com.food.delivery.service.dto.IngredientRepresentation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.food.delivery.domain.IngredientModel}.
 */
public interface IngredientService {
    /**
     * Save a ingredient.
     *
     * @param ingredientRepresentation the entity to save.
     * @return the persisted entity.
     */
    IngredientRepresentation save(IngredientRepresentation ingredientRepresentation);

    /**
     * Updates a ingredient.
     *
     * @param ingredientRepresentation the entity to update.
     * @return the persisted entity.
     */
    IngredientRepresentation update(IngredientRepresentation ingredientRepresentation);

    /**
     * Partially updates a ingredient.
     *
     * @param ingredientRepresentation the entity to update partially.
     * @return the persisted entity.
     */
    Optional<IngredientRepresentation> partialUpdate(IngredientRepresentation ingredientRepresentation);

    /**
     * Get all the ingredients.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<IngredientRepresentation> findAll(Pageable pageable);

    /**
     * Get the "id" ingredient.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<IngredientRepresentation> findOne(Long id);

    /**
     * Delete the "id" ingredient.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
