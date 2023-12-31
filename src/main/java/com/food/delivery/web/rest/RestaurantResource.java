package com.food.delivery.web.rest;

import com.food.delivery.repository.RestaurantRepository;
import com.food.delivery.service.RestaurantQueryService;
import com.food.delivery.service.RestaurantService;
import com.food.delivery.service.criteria.RestaurantCriteria;
import com.food.delivery.service.dto.RestaurantRepresentation;
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
 * REST controller for managing {@link com.food.delivery.domain.RestaurantModel}.
 */
@RestController
@RequestMapping("/api")
public class RestaurantResource {

    private final Logger log = LoggerFactory.getLogger(RestaurantResource.class);

    private static final String ENTITY_NAME = "restaurant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestaurantService restaurantService;

    private final RestaurantRepository restaurantRepository;

    private final RestaurantQueryService restaurantQueryService;

    public RestaurantResource(
        RestaurantService restaurantService,
        RestaurantRepository restaurantRepository,
        RestaurantQueryService restaurantQueryService
    ) {
        this.restaurantService = restaurantService;
        this.restaurantRepository = restaurantRepository;
        this.restaurantQueryService = restaurantQueryService;
    }

    /**
     * {@code POST  /restaurants} : Create a new restaurant.
     *
     * @param restaurantRepresentation the restaurantRepresentation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurantRepresentation, or with status {@code 400 (Bad Request)} if the restaurant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantRepresentation> createRestaurant(@Valid @RequestBody RestaurantRepresentation restaurantRepresentation)
        throws URISyntaxException {
        log.debug("REST request to save Restaurant : {}", restaurantRepresentation);
        if (restaurantRepresentation.getId() != null) {
            throw new BadRequestAlertException("A new restaurant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RestaurantRepresentation result = restaurantService.save(restaurantRepresentation);
        return ResponseEntity
            .created(new URI("/api/restaurants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /restaurants/:id} : Updates an existing restaurant.
     *
     * @param id the id of the restaurantRepresentation to save.
     * @param restaurantRepresentation the restaurantRepresentation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantRepresentation,
     * or with status {@code 400 (Bad Request)} if the restaurantRepresentation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restaurantRepresentation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantRepresentation> updateRestaurant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RestaurantRepresentation restaurantRepresentation
    ) throws URISyntaxException {
        log.debug("REST request to update Restaurant : {}, {}", id, restaurantRepresentation);
        if (restaurantRepresentation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantRepresentation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!restaurantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RestaurantRepresentation result = restaurantService.update(restaurantRepresentation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, restaurantRepresentation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /restaurants/:id} : Partial updates given fields of an existing restaurant, field will ignore if it is null
     *
     * @param id the id of the restaurantRepresentation to save.
     * @param restaurantRepresentation the restaurantRepresentation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantRepresentation,
     * or with status {@code 400 (Bad Request)} if the restaurantRepresentation is not valid,
     * or with status {@code 404 (Not Found)} if the restaurantRepresentation is not found,
     * or with status {@code 500 (Internal Server Error)} if the restaurantRepresentation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/restaurants/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RestaurantRepresentation> partialUpdateRestaurant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RestaurantRepresentation restaurantRepresentation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Restaurant partially : {}, {}", id, restaurantRepresentation);
        if (restaurantRepresentation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantRepresentation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!restaurantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RestaurantRepresentation> result = restaurantService.partialUpdate(restaurantRepresentation);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, restaurantRepresentation.getId().toString())
        );
    }

    /**
     * {@code GET  /restaurants} : get all the restaurants.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurants in body.
     */
    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantRepresentation>> getAllRestaurants(
        RestaurantCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Restaurants by criteria: {}", criteria);
        Page<RestaurantRepresentation> page = restaurantQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /restaurants/count} : count all the restaurants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/restaurants/count")
    public ResponseEntity<Long> countRestaurants(RestaurantCriteria criteria) {
        log.debug("REST request to count Restaurants by criteria: {}", criteria);
        return ResponseEntity.ok().body(restaurantQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /restaurants/:id} : get the "id" restaurant.
     *
     * @param id the id of the restaurantRepresentation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurantRepresentation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantRepresentation> getRestaurant(@PathVariable Long id) {
        log.debug("REST request to get Restaurant : {}", id);
        Optional<RestaurantRepresentation> restaurantRepresentation = restaurantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(restaurantRepresentation);
    }

    /**
     * {@code DELETE  /restaurants/:id} : delete the "id" restaurant.
     *
     * @param id the id of the restaurantRepresentation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        log.debug("REST request to delete Restaurant : {}", id);
        restaurantService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
