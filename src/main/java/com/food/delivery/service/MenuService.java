package com.food.delivery.service;

import com.food.delivery.service.dto.MenuRepresentation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuService {
    MenuRepresentation save(MenuRepresentation menuRepresentation);

    MenuRepresentation update(MenuRepresentation menuRepresentation);

    Optional<MenuRepresentation> partialUpdate(MenuRepresentation menuRepresentation);

    Page<MenuRepresentation> findAll(Pageable pageable);

    Page<MenuRepresentation> findAllWithEagerRelationships(Pageable pageable);

    Optional<MenuRepresentation> findOne(Long id);

    void delete(Long id);
}
