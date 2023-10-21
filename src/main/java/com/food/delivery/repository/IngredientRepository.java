package com.food.delivery.repository;

import com.food.delivery.domain.IngredientModel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the IngredientModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IngredientRepository extends JpaRepository<IngredientModel, Long>, JpaSpecificationExecutor<IngredientModel> {}
