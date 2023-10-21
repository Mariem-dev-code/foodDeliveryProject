package com.food.delivery.web.rest;

import com.food.delivery.repository.MenuRepository;
import com.food.delivery.service.MenuQueryService;
import com.food.delivery.service.MenuService;
import com.food.delivery.service.criteria.MenuCriteria;
import com.food.delivery.service.dto.MenuRepresentation;
import com.food.delivery.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.food.delivery.domain.MenuModel}.
 */
@RestController
@RequestMapping("/api")
public class MenuResource {

    private final Logger log = LoggerFactory.getLogger(MenuResource.class);

    private static final String ENTITY_NAME = "menu";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuService menuService;

    private final MenuRepository menuRepository;

    private final MenuQueryService menuQueryService;

    public MenuResource(MenuService menuService, MenuRepository menuRepository, MenuQueryService menuQueryService) {
        this.menuService = menuService;
        this.menuRepository = menuRepository;
        this.menuQueryService = menuQueryService;
    }

    /**
     * {@code POST  /menus} : Create a new menu.
     *
     * @param menuRepresentation the menuRepresentation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menuRepresentation, or with status {@code 400 (Bad Request)} if the menu has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/menus")
    public ResponseEntity<MenuRepresentation> createMenu(@Valid @RequestBody MenuRepresentation menuRepresentation)
        throws URISyntaxException {
        log.debug("REST request to save Menu : {}", menuRepresentation);
        if (menuRepresentation.getId() != null) {
            throw new BadRequestAlertException("A new menu cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MenuRepresentation result = menuService.save(menuRepresentation);
        return ResponseEntity
            .created(new URI("/api/menus/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /menus/:id} : Updates an existing menu.
     *
     * @param id the id of the menuRepresentation to save.
     * @param menuRepresentation the menuRepresentation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuRepresentation,
     * or with status {@code 400 (Bad Request)} if the menuRepresentation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menuRepresentation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/menus/{id}")
    public ResponseEntity<MenuRepresentation> updateMenu(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MenuRepresentation menuRepresentation
    ) throws URISyntaxException {
        log.debug("REST request to update Menu : {}, {}", id, menuRepresentation);
        if (menuRepresentation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuRepresentation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MenuRepresentation result = menuService.update(menuRepresentation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuRepresentation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /menus/:id} : Partial updates given fields of an existing menu, field will ignore if it is null
     *
     * @param id the id of the menuRepresentation to save.
     * @param menuRepresentation the menuRepresentation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menuRepresentation,
     * or with status {@code 400 (Bad Request)} if the menuRepresentation is not valid,
     * or with status {@code 404 (Not Found)} if the menuRepresentation is not found,
     * or with status {@code 500 (Internal Server Error)} if the menuRepresentation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/menus/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MenuRepresentation> partialUpdateMenu(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MenuRepresentation menuRepresentation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Menu partially : {}, {}", id, menuRepresentation);
        if (menuRepresentation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menuRepresentation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!menuRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MenuRepresentation> result = menuService.partialUpdate(menuRepresentation);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, menuRepresentation.getId().toString())
        );
    }

    /**
     * {@code GET  /menus} : get all the menus.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menus in body.
     */
    @GetMapping("/menus")
    public ResponseEntity<List<MenuRepresentation>> getAllMenus(
        MenuCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Menus by criteria: {}", criteria);
        Page<MenuRepresentation> page = menuQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /menus/count} : count all the menus.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/menus/count")
    public ResponseEntity<Long> countMenus(MenuCriteria criteria) {
        log.debug("REST request to count Menus by criteria: {}", criteria);
        return ResponseEntity.ok().body(menuQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /menus/:id} : get the "id" menu.
     *
     * @param id the id of the menuRepresentation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menuRepresentation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/menus/{id}")
    public ResponseEntity<MenuRepresentation> getMenu(@PathVariable Long id) {
        log.debug("REST request to get Menu : {}", id);
        Optional<MenuRepresentation> menuRepresentation = menuService.findOne(id);
        return ResponseUtil.wrapOrNotFound(menuRepresentation);
    }

    /**
     * {@code DELETE  /menus/:id} : delete the "id" menu.
     *
     * @param id the id of the menuRepresentation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/menus/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        log.debug("REST request to delete Menu : {}", id);
        menuService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
