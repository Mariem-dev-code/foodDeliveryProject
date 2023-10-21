package com.food.delivery.repository;

import com.food.delivery.domain.MenuModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface MenuRepositoryWithBagRelationships {
    Optional<MenuModel> fetchBagRelationships(Optional<MenuModel> menu);

    List<MenuModel> fetchBagRelationships(List<MenuModel> menus);

    Page<MenuModel> fetchBagRelationships(Page<MenuModel> menus);
}
