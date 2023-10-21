package com.food.delivery.web.rest;

import com.food.delivery.repository.TagRepository;
import com.food.delivery.service.TagQueryService;
import com.food.delivery.service.TagService;
import com.food.delivery.service.criteria.TagCriteria;
import com.food.delivery.service.dto.TagRepresentation;
import com.food.delivery.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link com.food.delivery.domain.TagModel}.
 */
@RestController
@RequestMapping("/api")
public class TagResource {

    private final Logger log = LoggerFactory.getLogger(TagResource.class);

    private static final String ENTITY_NAME = "tag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TagService tagService;

    private final TagRepository tagRepository;

    private final TagQueryService tagQueryService;

    public TagResource(TagService tagService, TagRepository tagRepository, TagQueryService tagQueryService) {
        this.tagService = tagService;
        this.tagRepository = tagRepository;
        this.tagQueryService = tagQueryService;
    }

    /**
     * {@code POST  /tags} : Create a new tag.
     *
     * @param tagRepresentation the tagRepresentation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tagRepresentation, or with status {@code 400 (Bad Request)} if the tag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tags")
    public ResponseEntity<TagRepresentation> createTag(@RequestBody TagRepresentation tagRepresentation) throws URISyntaxException {
        log.debug("REST request to save Tag : {}", tagRepresentation);
        if (tagRepresentation.getId() != null) {
            throw new BadRequestAlertException("A new tag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TagRepresentation result = tagService.save(tagRepresentation);
        return ResponseEntity
            .created(new URI("/api/tags/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tags/:id} : Updates an existing tag.
     *
     * @param id the id of the tagRepresentation to save.
     * @param tagRepresentation the tagRepresentation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagRepresentation,
     * or with status {@code 400 (Bad Request)} if the tagRepresentation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tagRepresentation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tags/{id}")
    public ResponseEntity<TagRepresentation> updateTag(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TagRepresentation tagRepresentation
    ) throws URISyntaxException {
        log.debug("REST request to update Tag : {}, {}", id, tagRepresentation);
        if (tagRepresentation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagRepresentation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TagRepresentation result = tagService.update(tagRepresentation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tagRepresentation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tags/:id} : Partial updates given fields of an existing tag, field will ignore if it is null
     *
     * @param id the id of the tagRepresentation to save.
     * @param tagRepresentation the tagRepresentation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagRepresentation,
     * or with status {@code 400 (Bad Request)} if the tagRepresentation is not valid,
     * or with status {@code 404 (Not Found)} if the tagRepresentation is not found,
     * or with status {@code 500 (Internal Server Error)} if the tagRepresentation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tags/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TagRepresentation> partialUpdateTag(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TagRepresentation tagRepresentation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tag partially : {}, {}", id, tagRepresentation);
        if (tagRepresentation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagRepresentation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TagRepresentation> result = tagService.partialUpdate(tagRepresentation);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tagRepresentation.getId().toString())
        );
    }

    /**
     * {@code GET  /tags} : get all the tags.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body.
     */
    @GetMapping("/tags")
    public ResponseEntity<List<TagRepresentation>> getAllTags(
        TagCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Tags by criteria: {}", criteria);
        Page<TagRepresentation> page = tagQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tags/count} : count all the tags.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tags/count")
    public ResponseEntity<Long> countTags(TagCriteria criteria) {
        log.debug("REST request to count Tags by criteria: {}", criteria);
        return ResponseEntity.ok().body(tagQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tags/:id} : get the "id" tag.
     *
     * @param id the id of the tagRepresentation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tagRepresentation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tags/{id}")
    public ResponseEntity<TagRepresentation> getTag(@PathVariable Long id) {
        log.debug("REST request to get Tag : {}", id);
        Optional<TagRepresentation> tagRepresentation = tagService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tagRepresentation);
    }

    /**
     * {@code DELETE  /tags/:id} : delete the "id" tag.
     *
     * @param id the id of the tagRepresentation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        log.debug("REST request to delete Tag : {}", id);
        tagService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
