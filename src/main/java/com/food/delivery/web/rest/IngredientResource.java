package com.food.delivery.web.rest;

import com.food.delivery.repository.IngredientRepository;
import com.food.delivery.service.IngredientQueryService;
import com.food.delivery.service.IngredientService;
import com.food.delivery.service.criteria.IngredientCriteria;
import com.food.delivery.service.dto.IngredientRepresentation;
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
 * REST controller for managing {@link com.food.delivery.domain.IngredientModel}.
 */
@RestController
@RequestMapping("/api")
public class IngredientResource {

    private final Logger log = LoggerFactory.getLogger(IngredientResource.class);

    private static final String ENTITY_NAME = "ingredient";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IngredientService ingredientService;

    private final IngredientRepository ingredientRepository;

    private final IngredientQueryService ingredientQueryService;

    public IngredientResource(
        IngredientService ingredientService,
        IngredientRepository ingredientRepository,
        IngredientQueryService ingredientQueryService
    ) {
        this.ingredientService = ingredientService;
        this.ingredientRepository = ingredientRepository;
        this.ingredientQueryService = ingredientQueryService;
    }

    /**
     * {@code POST  /ingredients} : Create a new ingredient.
     *
     * @param ingredientRepresentation the ingredientRepresentation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ingredientRepresentation, or with status {@code 400 (Bad Request)} if the ingredient has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ingredients")
    public ResponseEntity<IngredientRepresentation> createIngredient(@Valid @RequestBody IngredientRepresentation ingredientRepresentation)
        throws URISyntaxException {
        log.debug("REST request to save Ingredient : {}", ingredientRepresentation);
        if (ingredientRepresentation.getId() != null) {
            throw new BadRequestAlertException("A new ingredient cannot already have an ID", ENTITY_NAME, "idexists");
        }
        IngredientRepresentation result = ingredientService.save(ingredientRepresentation);
        return ResponseEntity
            .created(new URI("/api/ingredients/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ingredients/:id} : Updates an existing ingredient.
     *
     * @param id the id of the ingredientRepresentation to save.
     * @param ingredientRepresentation the ingredientRepresentation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ingredientRepresentation,
     * or with status {@code 400 (Bad Request)} if the ingredientRepresentation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ingredientRepresentation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ingredients/{id}")
    public ResponseEntity<IngredientRepresentation> updateIngredient(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IngredientRepresentation ingredientRepresentation
    ) throws URISyntaxException {
        log.debug("REST request to update Ingredient : {}, {}", id, ingredientRepresentation);
        if (ingredientRepresentation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ingredientRepresentation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ingredientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        IngredientRepresentation result = ingredientService.update(ingredientRepresentation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ingredientRepresentation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ingredients/:id} : Partial updates given fields of an existing ingredient, field will ignore if it is null
     *
     * @param id the id of the ingredientRepresentation to save.
     * @param ingredientRepresentation the ingredientRepresentation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ingredientRepresentation,
     * or with status {@code 400 (Bad Request)} if the ingredientRepresentation is not valid,
     * or with status {@code 404 (Not Found)} if the ingredientRepresentation is not found,
     * or with status {@code 500 (Internal Server Error)} if the ingredientRepresentation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ingredients/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IngredientRepresentation> partialUpdateIngredient(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IngredientRepresentation ingredientRepresentation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Ingredient partially : {}, {}", id, ingredientRepresentation);
        if (ingredientRepresentation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ingredientRepresentation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ingredientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IngredientRepresentation> result = ingredientService.partialUpdate(ingredientRepresentation);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ingredientRepresentation.getId().toString())
        );
    }

    /**
     * {@code GET  /ingredients} : get all the ingredients.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ingredients in body.
     */
    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientRepresentation>> getAllIngredients(
        IngredientCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Ingredients by criteria: {}", criteria);
        Page<IngredientRepresentation> page = ingredientQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ingredients/count} : count all the ingredients.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/ingredients/count")
    public ResponseEntity<Long> countIngredients(IngredientCriteria criteria) {
        log.debug("REST request to count Ingredients by criteria: {}", criteria);
        return ResponseEntity.ok().body(ingredientQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ingredients/:id} : get the "id" ingredient.
     *
     * @param id the id of the ingredientRepresentation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ingredientRepresentation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ingredients/{id}")
    public ResponseEntity<IngredientRepresentation> getIngredient(@PathVariable Long id) {
        log.debug("REST request to get Ingredient : {}", id);
        Optional<IngredientRepresentation> ingredientRepresentation = ingredientService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ingredientRepresentation);
    }

    /**
     * {@code DELETE  /ingredients/:id} : delete the "id" ingredient.
     *
     * @param id the id of the ingredientRepresentation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ingredients/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        log.debug("REST request to delete Ingredient : {}", id);
        ingredientService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
