package com.food.delivery.repository;

import com.food.delivery.domain.MenuModel;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class MenuRepositoryWithBagRelationshipsImpl implements MenuRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<MenuModel> fetchBagRelationships(Optional<MenuModel> menu) {
        return menu.map(this::fetchIngredients).map(this::fetchTags);
    }

    @Override
    public Page<MenuModel> fetchBagRelationships(Page<MenuModel> menus) {
        return new PageImpl<>(fetchBagRelationships(menus.getContent()), menus.getPageable(), menus.getTotalElements());
    }

    @Override
    public List<MenuModel> fetchBagRelationships(List<MenuModel> menus) {
        return Optional.of(menus).map(this::fetchIngredients).map(this::fetchTags).orElse(Collections.emptyList());
    }

    MenuModel fetchIngredients(MenuModel result) {
        return entityManager
            .createQuery("select menu from MenuModel menu left join fetch menu.ingredients where menu is :menu", MenuModel.class)
            .setParameter("menu", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<MenuModel> fetchIngredients(List<MenuModel> menus) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, menus.size()).forEach(index -> order.put(menus.get(index).getId(), index));
        List<MenuModel> result = entityManager
            .createQuery("select distinct menu from MenuModel menu left join fetch menu.ingredients where menu in :menus", MenuModel.class)
            .setParameter("menus", menus)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    MenuModel fetchTags(MenuModel result) {
        return entityManager
            .createQuery("select menu from MenuModel menu left join fetch menu.tags where menu is :menu", MenuModel.class)
            .setParameter("menu", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<MenuModel> fetchTags(List<MenuModel> menus) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, menus.size()).forEach(index -> order.put(menus.get(index).getId(), index));
        List<MenuModel> result = entityManager
            .createQuery("select distinct menu from MenuModel menu left join fetch menu.tags where menu in :menus", MenuModel.class)
            .setParameter("menus", menus)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
