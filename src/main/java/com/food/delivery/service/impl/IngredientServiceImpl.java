package com.food.delivery.service.impl;

import com.food.delivery.domain.IngredientModel;
import com.food.delivery.repository.IngredientRepository;
import com.food.delivery.service.IngredientService;
import com.food.delivery.service.dto.IngredientRepresentation;
import com.food.delivery.service.mapper.IngredientMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link IngredientModel}.
 */
@Service
@Transactional
public class IngredientServiceImpl implements IngredientService {

    private final Logger log = LoggerFactory.getLogger(IngredientServiceImpl.class);

    private final IngredientRepository ingredientRepository;

    private final IngredientMapper ingredientMapper;

    public IngredientServiceImpl(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    @Override
    public IngredientRepresentation save(IngredientRepresentation ingredientRepresentation) {
        log.debug("Request to save Ingredient : {}", ingredientRepresentation);
        IngredientModel ingredientModel = ingredientMapper.toEntity(ingredientRepresentation);
        ingredientModel = ingredientRepository.save(ingredientModel);
        return ingredientMapper.toDto(ingredientModel);
    }

    @Override
    public IngredientRepresentation update(IngredientRepresentation ingredientRepresentation) {
        log.debug("Request to update Ingredient : {}", ingredientRepresentation);
        IngredientModel ingredientModel = ingredientMapper.toEntity(ingredientRepresentation);
        ingredientModel = ingredientRepository.save(ingredientModel);
        return ingredientMapper.toDto(ingredientModel);
    }

    @Override
    public Optional<IngredientRepresentation> partialUpdate(IngredientRepresentation ingredientRepresentation) {
        log.debug("Request to partially update Ingredient : {}", ingredientRepresentation);

        return ingredientRepository
            .findById(ingredientRepresentation.getId())
            .map(existingIngredient -> {
                ingredientMapper.partialUpdate(existingIngredient, ingredientRepresentation);

                return existingIngredient;
            })
            .map(ingredientRepository::save)
            .map(ingredientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IngredientRepresentation> findAll(Pageable pageable) {
        log.debug("Request to get all Ingredients");
        return ingredientRepository.findAll(pageable).map(ingredientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IngredientRepresentation> findOne(Long id) {
        log.debug("Request to get Ingredient : {}", id);
        return ingredientRepository.findById(id).map(ingredientMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Ingredient : {}", id);
        ingredientRepository.deleteById(id);
    }
}
