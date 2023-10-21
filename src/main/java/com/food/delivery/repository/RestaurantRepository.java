package com.food.delivery.repository;

import com.food.delivery.domain.RestaurantModel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RestaurantModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantModel, Long>, JpaSpecificationExecutor<RestaurantModel> {}
