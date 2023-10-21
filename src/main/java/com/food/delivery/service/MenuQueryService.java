package com.food.delivery.service;

import com.food.delivery.domain.*; // for static metamodels
import com.food.delivery.domain.MenuModel;
import com.food.delivery.repository.MenuRepository;
import com.food.delivery.service.criteria.MenuCriteria;
import com.food.delivery.service.dto.MenuRepresentation;
import com.food.delivery.service.mapper.MenuMapper;
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
 * Service for executing complex queries for {@link MenuModel} entities in the database.
 * The main input is a {@link MenuCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MenuRepresentation} or a {@link Page} of {@link MenuRepresentation} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MenuQueryService extends QueryService<MenuModel> {

    private final Logger log = LoggerFactory.getLogger(MenuQueryService.class);

    private final MenuRepository menuRepository;

    private final MenuMapper menuMapper;

    public MenuQueryService(MenuRepository menuRepository, MenuMapper menuMapper) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
    }

    /**
     * Return a {@link List} of {@link MenuRepresentation} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MenuRepresentation> findByCriteria(MenuCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<MenuModel> specification = createSpecification(criteria);
        return menuMapper.toDto(menuRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MenuRepresentation} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MenuRepresentation> findByCriteria(MenuCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MenuModel> specification = createSpecification(criteria);
        return menuRepository.findAll(specification, page).map(menuMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MenuCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<MenuModel> specification = createSpecification(criteria);
        return menuRepository.count(specification);
    }

    /**
     * Function to convert {@link MenuCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MenuModel> createSpecification(MenuCriteria criteria) {
        Specification<MenuModel> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), MenuModel_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), MenuModel_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), MenuModel_.description));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), MenuModel_.price));
            }
            if (criteria.getRestaurantId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRestaurantId(),
                            root -> root.join(MenuModel_.restaurant, JoinType.LEFT).get(RestaurantModel_.id)
                        )
                    );
            }
            if (criteria.getIngredientId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getIngredientId(),
                            root -> root.join(MenuModel_.ingredients, JoinType.LEFT).get(IngredientModel_.id)
                        )
                    );
            }
            if (criteria.getTagId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTagId(), root -> root.join(MenuModel_.tags, JoinType.LEFT).get(TagModel_.id))
                    );
            }
        }
        return specification;
    }
}
