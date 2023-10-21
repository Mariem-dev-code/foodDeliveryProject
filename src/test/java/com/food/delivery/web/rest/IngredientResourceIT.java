package com.food.delivery.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.food.delivery.IntegrationTest;
import com.food.delivery.domain.IngredientModel;
import com.food.delivery.domain.MenuModel;
import com.food.delivery.repository.IngredientRepository;
import com.food.delivery.service.criteria.IngredientCriteria;
import com.food.delivery.service.dto.IngredientRepresentation;
import com.food.delivery.service.mapper.IngredientMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link IngredientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IngredientResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ingredients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientMapper ingredientMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIngredientMockMvc;

    private IngredientModel ingredientModel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IngredientModel createEntity(EntityManager em) {
        IngredientModel ingredientModel = new IngredientModel().name(DEFAULT_NAME);
        return ingredientModel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IngredientModel createUpdatedEntity(EntityManager em) {
        IngredientModel ingredientModel = new IngredientModel().name(UPDATED_NAME);
        return ingredientModel;
    }

    @BeforeEach
    public void initTest() {
        ingredientModel = createEntity(em);
    }

    @Test
    @Transactional
    void createIngredient() throws Exception {
        int databaseSizeBeforeCreate = ingredientRepository.findAll().size();
        // Create the Ingredient
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(ingredientModel);
        restIngredientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isCreated());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeCreate + 1);
        IngredientModel testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createIngredientWithExistingId() throws Exception {
        // Create the Ingredient with an existing ID
        ingredientModel.setId(1L);
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(ingredientModel);

        int databaseSizeBeforeCreate = ingredientRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIngredientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ingredientRepository.findAll().size();
        // set the field null
        ingredientModel.setName(null);

        // Create the Ingredient, which fails.
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(ingredientModel);

        restIngredientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isBadRequest());

        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIngredients() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        // Get all the ingredientList
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredientModel.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        // Get the ingredient
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL_ID, ingredientModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ingredientModel.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getIngredientsByIdFiltering() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        Long id = ingredientModel.getId();

        defaultIngredientShouldBeFound("id.equals=" + id);
        defaultIngredientShouldNotBeFound("id.notEquals=" + id);

        defaultIngredientShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultIngredientShouldNotBeFound("id.greaterThan=" + id);

        defaultIngredientShouldBeFound("id.lessThanOrEqual=" + id);
        defaultIngredientShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        // Get all the ingredientList where name equals to DEFAULT_NAME
        defaultIngredientShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the ingredientList where name equals to UPDATED_NAME
        defaultIngredientShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        // Get all the ingredientList where name in DEFAULT_NAME or UPDATED_NAME
        defaultIngredientShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the ingredientList where name equals to UPDATED_NAME
        defaultIngredientShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        // Get all the ingredientList where name is not null
        defaultIngredientShouldBeFound("name.specified=true");

        // Get all the ingredientList where name is null
        defaultIngredientShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllIngredientsByNameContainsSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        // Get all the ingredientList where name contains DEFAULT_NAME
        defaultIngredientShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the ingredientList where name contains UPDATED_NAME
        defaultIngredientShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        // Get all the ingredientList where name does not contain DEFAULT_NAME
        defaultIngredientShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the ingredientList where name does not contain UPDATED_NAME
        defaultIngredientShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllIngredientsByMenuIsEqualToSomething() throws Exception {
        MenuModel menu;
        if (TestUtil.findAll(em, MenuModel.class).isEmpty()) {
            ingredientRepository.saveAndFlush(ingredientModel);
            menu = MenuResourceIT.createEntity(em);
        } else {
            menu = TestUtil.findAll(em, MenuModel.class).get(0);
        }
        em.persist(menu);
        em.flush();
        ingredientModel.addMenu(menu);
        ingredientRepository.saveAndFlush(ingredientModel);
        Long menuId = menu.getId();

        // Get all the ingredientList where menu equals to menuId
        defaultIngredientShouldBeFound("menuId.equals=" + menuId);

        // Get all the ingredientList where menu equals to (menuId + 1)
        defaultIngredientShouldNotBeFound("menuId.equals=" + (menuId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIngredientShouldBeFound(String filter) throws Exception {
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ingredientModel.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIngredientShouldNotBeFound(String filter) throws Exception {
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIngredientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingIngredient() throws Exception {
        // Get the ingredient
        restIngredientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();

        // Update the ingredient
        IngredientModel updatedIngredientModel = ingredientRepository.findById(ingredientModel.getId()).get();
        // Disconnect from session so that the updates on updatedIngredientModel are not directly saved in db
        em.detach(updatedIngredientModel);
        updatedIngredientModel.name(UPDATED_NAME);
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(updatedIngredientModel);

        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ingredientRepresentation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        IngredientModel testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredientModel.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(ingredientModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ingredientRepresentation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredientModel.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(ingredientModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredientModel.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(ingredientModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIngredientWithPatch() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();

        // Update the ingredient using partial update
        IngredientModel partialUpdatedIngredientModel = new IngredientModel();
        partialUpdatedIngredientModel.setId(ingredientModel.getId());

        partialUpdatedIngredientModel.name(UPDATED_NAME);

        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredientModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredientModel))
            )
            .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        IngredientModel testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateIngredientWithPatch() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();

        // Update the ingredient using partial update
        IngredientModel partialUpdatedIngredientModel = new IngredientModel();
        partialUpdatedIngredientModel.setId(ingredientModel.getId());

        partialUpdatedIngredientModel.name(UPDATED_NAME);

        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIngredientModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIngredientModel))
            )
            .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
        IngredientModel testIngredient = ingredientList.get(ingredientList.size() - 1);
        assertThat(testIngredient.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredientModel.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(ingredientModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ingredientRepresentation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredientModel.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(ingredientModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIngredient() throws Exception {
        int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();
        ingredientModel.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientRepresentation ingredientRepresentation = ingredientMapper.toDto(ingredientModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ingredientRepresentation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ingredient in the database
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredientModel);

        int databaseSizeBeforeDelete = ingredientRepository.findAll().size();

        // Delete the ingredient
        restIngredientMockMvc
            .perform(delete(ENTITY_API_URL_ID, ingredientModel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<IngredientModel> ingredientList = ingredientRepository.findAll();
        assertThat(ingredientList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
