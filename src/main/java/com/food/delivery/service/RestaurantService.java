package com.food.delivery.service;

import com.food.delivery.service.dto.RestaurantRepresentation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.food.delivery.domain.RestaurantModel}.
 */
public interface RestaurantService {
    /**
     * Save a restaurant.
     *
     * @param restaurantRepresentation the entity to save.
     * @return the persisted entity.
     */
    RestaurantRepresentation save(RestaurantRepresentation restaurantRepresentation);

    /**
     * Updates a restaurant.
     *
     * @param restaurantRepresentation the entity to update.
     * @return the persisted entity.
     */
    RestaurantRepresentation update(RestaurantRepresentation restaurantRepresentation);

    /**
     * Partially updates a restaurant.
     *
     * @param restaurantRepresentation the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RestaurantRepresentation> partialUpdate(RestaurantRepresentation restaurantRepresentation);

    /**
     * Get all the restaurants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RestaurantRepresentation> findAll(Pageable pageable);

    /**
     * Get the "id" restaurant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RestaurantRepresentation> findOne(Long id);

    /**
     * Delete the "id" restaurant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
