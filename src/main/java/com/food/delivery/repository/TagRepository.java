package com.food.delivery.repository;

import com.food.delivery.domain.TagModel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TagModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends JpaRepository<TagModel, Long>, JpaSpecificationExecutor<TagModel> {}
