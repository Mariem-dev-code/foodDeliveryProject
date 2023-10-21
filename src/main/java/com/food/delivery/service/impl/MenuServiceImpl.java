package com.food.delivery.service.impl;

import com.food.delivery.domain.MenuModel;
import com.food.delivery.repository.MenuRepository;
import com.food.delivery.service.MenuService;
import com.food.delivery.service.dto.MenuRepresentation;
import com.food.delivery.service.mapper.MenuMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MenuModel}.
 */
@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    private final Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final MenuRepository menuRepository;

    private final MenuMapper menuMapper;

    public MenuServiceImpl(MenuRepository menuRepository, MenuMapper menuMapper) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
    }

    @Override
    public MenuRepresentation save(MenuRepresentation menuRepresentation) {
        log.debug("Request to save Menu : {}", menuRepresentation);
        MenuModel menuModel = menuMapper.toEntity(menuRepresentation);
        menuModel = menuRepository.save(menuModel);
        return menuMapper.toDto(menuModel);
    }

    @Override
    public MenuRepresentation update(MenuRepresentation menuRepresentation) {
        log.debug("Request to update Menu : {}", menuRepresentation);
        MenuModel menuModel = menuMapper.toEntity(menuRepresentation);
        menuModel = menuRepository.save(menuModel);
        return menuMapper.toDto(menuModel);
    }

    @Override
    public Optional<MenuRepresentation> partialUpdate(MenuRepresentation menuRepresentation) {
        log.debug("Request to partially update Menu : {}", menuRepresentation);

        return menuRepository
            .findById(menuRepresentation.getId())
            .map(existingMenu -> {
                menuMapper.partialUpdate(existingMenu, menuRepresentation);

                return existingMenu;
            })
            .map(menuRepository::save)
            .map(menuMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuRepresentation> findAll(Pageable pageable) {
        log.debug("Request to get all Menus");
        return menuRepository.findAll(pageable).map(menuMapper::toDto);
    }

    public Page<MenuRepresentation> findAllWithEagerRelationships(Pageable pageable) {
        return menuRepository.findAllWithEagerRelationships(pageable).map(menuMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuRepresentation> findOne(Long id) {
        log.debug("Request to get Menu : {}", id);
        return menuRepository.findOneWithEagerRelationships(id).map(menuMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Menu : {}", id);
        menuRepository.deleteById(id);
    }
}
