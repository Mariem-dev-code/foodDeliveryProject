package com.food.delivery.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.food.delivery.IntegrationTest;
import com.food.delivery.domain.RestaurantModel;
import com.food.delivery.repository.RestaurantRepository;
import com.food.delivery.service.criteria.RestaurantCriteria;
import com.food.delivery.service.dto.RestaurantRepresentation;
import com.food.delivery.service.mapper.RestaurantMapper;
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
 * Integration tests for the {@link RestaurantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RestaurantResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADRESS = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final String ENTITY_API_URL = "/api/restaurants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRestaurantMockMvc;

    private RestaurantModel restaurantModel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantModel createEntity(EntityManager em) {
        RestaurantModel restaurantModel = new RestaurantModel()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .adress(DEFAULT_ADRESS)
            .rating(DEFAULT_RATING);
        return restaurantModel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantModel createUpdatedEntity(EntityManager em) {
        RestaurantModel restaurantModel = new RestaurantModel()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .adress(UPDATED_ADRESS)
            .rating(UPDATED_RATING);
        return restaurantModel;
    }

    @BeforeEach
    public void initTest() {
        restaurantModel = createEntity(em);
    }

    @Test
    @Transactional
    void createRestaurant() throws Exception {
        int databaseSizeBeforeCreate = restaurantRepository.findAll().size();
        // Create the Restaurant
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(restaurantModel);
        restRestaurantMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isCreated());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate + 1);
        RestaurantModel testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRestaurant.getAdress()).isEqualTo(DEFAULT_ADRESS);
        assertThat(testRestaurant.getRating()).isEqualTo(DEFAULT_RATING);
    }

    @Test
    @Transactional
    void createRestaurantWithExistingId() throws Exception {
        // Create the Restaurant with an existing ID
        restaurantModel.setId(1L);
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(restaurantModel);

        int databaseSizeBeforeCreate = restaurantRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRestaurantMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantRepository.findAll().size();
        // set the field null
        restaurantModel.setName(null);

        // Create the Restaurant, which fails.
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(restaurantModel);

        restRestaurantMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isBadRequest());

        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRestaurants() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurantModel.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].adress").value(hasItem(DEFAULT_ADRESS)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)));
    }

    @Test
    @Transactional
    void getRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get the restaurant
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL_ID, restaurantModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(restaurantModel.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.adress").value(DEFAULT_ADRESS))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING));
    }

    @Test
    @Transactional
    void getRestaurantsByIdFiltering() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        Long id = restaurantModel.getId();

        defaultRestaurantShouldBeFound("id.equals=" + id);
        defaultRestaurantShouldNotBeFound("id.notEquals=" + id);

        defaultRestaurantShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRestaurantShouldNotBeFound("id.greaterThan=" + id);

        defaultRestaurantShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRestaurantShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where name equals to DEFAULT_NAME
        defaultRestaurantShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the restaurantList where name equals to UPDATED_NAME
        defaultRestaurantShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where name in DEFAULT_NAME or UPDATED_NAME
        defaultRestaurantShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the restaurantList where name equals to UPDATED_NAME
        defaultRestaurantShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where name is not null
        defaultRestaurantShouldBeFound("name.specified=true");

        // Get all the restaurantList where name is null
        defaultRestaurantShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByNameContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where name contains DEFAULT_NAME
        defaultRestaurantShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the restaurantList where name contains UPDATED_NAME
        defaultRestaurantShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where name does not contain DEFAULT_NAME
        defaultRestaurantShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the restaurantList where name does not contain UPDATED_NAME
        defaultRestaurantShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where description equals to DEFAULT_DESCRIPTION
        defaultRestaurantShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the restaurantList where description equals to UPDATED_DESCRIPTION
        defaultRestaurantShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultRestaurantShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the restaurantList where description equals to UPDATED_DESCRIPTION
        defaultRestaurantShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where description is not null
        defaultRestaurantShouldBeFound("description.specified=true");

        // Get all the restaurantList where description is null
        defaultRestaurantShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where description contains DEFAULT_DESCRIPTION
        defaultRestaurantShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the restaurantList where description contains UPDATED_DESCRIPTION
        defaultRestaurantShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where description does not contain DEFAULT_DESCRIPTION
        defaultRestaurantShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the restaurantList where description does not contain UPDATED_DESCRIPTION
        defaultRestaurantShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdressIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where adress equals to DEFAULT_ADRESS
        defaultRestaurantShouldBeFound("adress.equals=" + DEFAULT_ADRESS);

        // Get all the restaurantList where adress equals to UPDATED_ADRESS
        defaultRestaurantShouldNotBeFound("adress.equals=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdressIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where adress in DEFAULT_ADRESS or UPDATED_ADRESS
        defaultRestaurantShouldBeFound("adress.in=" + DEFAULT_ADRESS + "," + UPDATED_ADRESS);

        // Get all the restaurantList where adress equals to UPDATED_ADRESS
        defaultRestaurantShouldNotBeFound("adress.in=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdressIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where adress is not null
        defaultRestaurantShouldBeFound("adress.specified=true");

        // Get all the restaurantList where adress is null
        defaultRestaurantShouldNotBeFound("adress.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdressContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where adress contains DEFAULT_ADRESS
        defaultRestaurantShouldBeFound("adress.contains=" + DEFAULT_ADRESS);

        // Get all the restaurantList where adress contains UPDATED_ADRESS
        defaultRestaurantShouldNotBeFound("adress.contains=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdressNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where adress does not contain DEFAULT_ADRESS
        defaultRestaurantShouldNotBeFound("adress.doesNotContain=" + DEFAULT_ADRESS);

        // Get all the restaurantList where adress does not contain UPDATED_ADRESS
        defaultRestaurantShouldBeFound("adress.doesNotContain=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllRestaurantsByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where rating equals to DEFAULT_RATING
        defaultRestaurantShouldBeFound("rating.equals=" + DEFAULT_RATING);

        // Get all the restaurantList where rating equals to UPDATED_RATING
        defaultRestaurantShouldNotBeFound("rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllRestaurantsByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where rating in DEFAULT_RATING or UPDATED_RATING
        defaultRestaurantShouldBeFound("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING);

        // Get all the restaurantList where rating equals to UPDATED_RATING
        defaultRestaurantShouldNotBeFound("rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllRestaurantsByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where rating is not null
        defaultRestaurantShouldBeFound("rating.specified=true");

        // Get all the restaurantList where rating is null
        defaultRestaurantShouldNotBeFound("rating.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where rating is greater than or equal to DEFAULT_RATING
        defaultRestaurantShouldBeFound("rating.greaterThanOrEqual=" + DEFAULT_RATING);

        // Get all the restaurantList where rating is greater than or equal to UPDATED_RATING
        defaultRestaurantShouldNotBeFound("rating.greaterThanOrEqual=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllRestaurantsByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where rating is less than or equal to DEFAULT_RATING
        defaultRestaurantShouldBeFound("rating.lessThanOrEqual=" + DEFAULT_RATING);

        // Get all the restaurantList where rating is less than or equal to SMALLER_RATING
        defaultRestaurantShouldNotBeFound("rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllRestaurantsByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where rating is less than DEFAULT_RATING
        defaultRestaurantShouldNotBeFound("rating.lessThan=" + DEFAULT_RATING);

        // Get all the restaurantList where rating is less than UPDATED_RATING
        defaultRestaurantShouldBeFound("rating.lessThan=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllRestaurantsByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        // Get all the restaurantList where rating is greater than DEFAULT_RATING
        defaultRestaurantShouldNotBeFound("rating.greaterThan=" + DEFAULT_RATING);

        // Get all the restaurantList where rating is greater than SMALLER_RATING
        defaultRestaurantShouldBeFound("rating.greaterThan=" + SMALLER_RATING);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRestaurantShouldBeFound(String filter) throws Exception {
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurantModel.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].adress").value(hasItem(DEFAULT_ADRESS)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)));

        // Check, that the count call also returns 1
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRestaurantShouldNotBeFound(String filter) throws Exception {
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRestaurant() throws Exception {
        // Get the restaurant
        restRestaurantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant
        RestaurantModel updatedRestaurantModel = restaurantRepository.findById(restaurantModel.getId()).get();
        // Disconnect from session so that the updates on updatedRestaurantModel are not directly saved in db
        em.detach(updatedRestaurantModel);
        updatedRestaurantModel.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).adress(UPDATED_ADRESS).rating(UPDATED_RATING);
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(updatedRestaurantModel);

        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantRepresentation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        RestaurantModel testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRestaurant.getAdress()).isEqualTo(UPDATED_ADRESS);
        assertThat(testRestaurant.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    @Transactional
    void putNonExistingRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurantModel.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(restaurantModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantRepresentation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurantModel.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(restaurantModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurantModel.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(restaurantModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant using partial update
        RestaurantModel partialUpdatedRestaurantModel = new RestaurantModel();
        partialUpdatedRestaurantModel.setId(restaurantModel.getId());

        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurantModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurantModel))
            )
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        RestaurantModel testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRestaurant.getAdress()).isEqualTo(DEFAULT_ADRESS);
        assertThat(testRestaurant.getRating()).isEqualTo(DEFAULT_RATING);
    }

    @Test
    @Transactional
    void fullUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant using partial update
        RestaurantModel partialUpdatedRestaurantModel = new RestaurantModel();
        partialUpdatedRestaurantModel.setId(restaurantModel.getId());

        partialUpdatedRestaurantModel.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).adress(UPDATED_ADRESS).rating(UPDATED_RATING);

        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurantModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurantModel))
            )
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        RestaurantModel testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRestaurant.getAdress()).isEqualTo(UPDATED_ADRESS);
        assertThat(testRestaurant.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    @Transactional
    void patchNonExistingRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurantModel.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(restaurantModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, restaurantRepresentation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurantModel.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(restaurantModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurantModel.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantRepresentation restaurantRepresentation = restaurantMapper.toDto(restaurantModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(restaurantRepresentation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Restaurant in the database
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurantModel);

        int databaseSizeBeforeDelete = restaurantRepository.findAll().size();

        // Delete the restaurant
        restRestaurantMockMvc
            .perform(delete(ENTITY_API_URL_ID, restaurantModel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RestaurantModel> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
