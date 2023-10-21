package com.food.delivery.service;

import com.food.delivery.domain.*; // for static metamodels
import com.food.delivery.domain.IngredientModel;
import com.food.delivery.repository.IngredientRepository;
import com.food.delivery.service.criteria.IngredientCriteria;
import com.food.delivery.service.dto.IngredientRepresentation;
import com.food.delivery.service.mapper.IngredientMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link IngredientModel} entities in the database.
 * The main input is a {@link IngredientCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link IngredientRepresentation} or a {@link Page} of {@link IngredientRepresentation} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class IngredientQueryService extends QueryService<IngredientModel> {

    private final Logger log = LoggerFactory.getLogger(IngredientQueryService.class);

    private final IngredientRepository ingredientRepository;

    private final IngredientMapper ingredientMapper;

    public IngredientQueryService(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    /**
     * Return a {@link List} of {@link IngredientRepresentation} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<IngredientRepresentation> findByCriteria(IngredientCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<IngredientModel> specification = createSpecification(criteria);
        return ingredientMapper.toDto(ingredientRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link IngredientRepresentation} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<IngredientRepresentation> findByCriteria(IngredientCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<IngredientModel> specification = createSpecification(criteria);
        return ingredientRepository.findAll(specification, page).map(ingredientMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(IngredientCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<IngredientModel> specification = createSpecification(criteria);
        return ingredientRepository.count(specification);
    }

    /**
     * Function to convert {@link IngredientCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<IngredientModel> createSpecification(IngredientCriteria criteria) {
        Specification<IngredientModel> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), IngredientModel_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), IngredientModel_.name));
            }
            if (criteria.getMenuId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getMenuId(),
                            root -> root.join(IngredientModel_.menus, JoinType.LEFT).get(MenuModel_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
