package com.food.delivery.service;

import com.food.delivery.service.dto.TagRepresentation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.food.delivery.domain.TagModel}.
 */
public interface TagService {
    /**
     * Save a tag.
     *
     * @param tagRepresentation the entity to save.
     * @return the persisted entity.
     */
    TagRepresentation save(TagRepresentation tagRepresentation);

    /**
     * Updates a tag.
     *
     * @param tagRepresentation the entity to update.
     * @return the persisted entity.
     */
    TagRepresentation update(TagRepresentation tagRepresentation);

    /**
     * Partially updates a tag.
     *
     * @param tagRepresentation the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TagRepresentation> partialUpdate(TagRepresentation tagRepresentation);

    /**
     * Get all the tags.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TagRepresentation> findAll(Pageable pageable);

    /**
     * Get the "id" tag.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TagRepresentation> findOne(Long id);

    /**
     * Delete the "id" tag.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
