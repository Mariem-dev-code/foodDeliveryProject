package com.food.delivery.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.food.delivery.IntegrationTest;
import com.food.delivery.domain.IngredientModel;
import com.food.delivery.domain.MenuModel;
import com.food.delivery.domain.RestaurantModel;
import com.food.delivery.domain.TagModel;
import com.food.delivery.repository.MenuRepository;
import com.food.delivery.service.MenuService;
import com.food.delivery.service.criteria.MenuCriteria;
import com.food.delivery.service.dto.MenuRepresentation;
import com.food.delivery.service.mapper.MenuMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MenuResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MenuResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Long DEFAULT_PRICE = 1L;
    private static final Long UPDATED_PRICE = 2L;
    private static final Long SMALLER_PRICE = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/menus";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MenuRepository menuRepository;

    @Mock
    private MenuRepository menuRepositoryMock;

    @Autowired
    private MenuMapper menuMapper;

    @Mock
    private MenuService menuServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMenuMockMvc;

    private MenuModel menuModel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuModel createEntity(EntityManager em) {
        MenuModel menuModel = new MenuModel().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).price(DEFAULT_PRICE);
        return menuModel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MenuModel createUpdatedEntity(EntityManager em) {
        MenuModel menuModel = new MenuModel().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).price(UPDATED_PRICE);
        return menuModel;
    }

    @BeforeEach
    public void initTest() {
        menuModel = createEntity(em);
    }

    @Test
    @Transactional
    void createMenu() throws Exception {
        int databaseSizeBeforeCreate = menuRepository.findAll().size();
        // Create the Menu
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);
        restMenuMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isCreated());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeCreate + 1);
        MenuModel testMenu = menuList.get(menuList.size() - 1);
        assertThat(testMenu.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMenu.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMenu.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void createMenuWithExistingId() throws Exception {
        // Create the Menu with an existing ID
        menuModel.setId(1L);
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);

        int databaseSizeBeforeCreate = menuRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMenuMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = menuRepository.findAll().size();
        // set the field null
        menuModel.setName(null);

        // Create the Menu, which fails.
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);

        restMenuMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isBadRequest());

        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = menuRepository.findAll().size();
        // set the field null
        menuModel.setPrice(null);

        // Create the Menu, which fails.
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);

        restMenuMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isBadRequest());

        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMenus() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList
        restMenuMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuModel.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenusWithEagerRelationshipsIsEnabled() throws Exception {
        when(menuServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(menuServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMenusWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(menuServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMenuMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(menuRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get the menu
        restMenuMockMvc
            .perform(get(ENTITY_API_URL_ID, menuModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(menuModel.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()));
    }

    @Test
    @Transactional
    void getMenusByIdFiltering() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        Long id = menuModel.getId();

        defaultMenuShouldBeFound("id.equals=" + id);
        defaultMenuShouldNotBeFound("id.notEquals=" + id);

        defaultMenuShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMenuShouldNotBeFound("id.greaterThan=" + id);

        defaultMenuShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMenuShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMenusByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where name equals to DEFAULT_NAME
        defaultMenuShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the menuList where name equals to UPDATED_NAME
        defaultMenuShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenusByNameIsInShouldWork() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMenuShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the menuList where name equals to UPDATED_NAME
        defaultMenuShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenusByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where name is not null
        defaultMenuShouldBeFound("name.specified=true");

        // Get all the menuList where name is null
        defaultMenuShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllMenusByNameContainsSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where name contains DEFAULT_NAME
        defaultMenuShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the menuList where name contains UPDATED_NAME
        defaultMenuShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenusByNameNotContainsSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where name does not contain DEFAULT_NAME
        defaultMenuShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the menuList where name does not contain UPDATED_NAME
        defaultMenuShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMenusByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where description equals to DEFAULT_DESCRIPTION
        defaultMenuShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the menuList where description equals to UPDATED_DESCRIPTION
        defaultMenuShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMenusByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultMenuShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the menuList where description equals to UPDATED_DESCRIPTION
        defaultMenuShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMenusByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where description is not null
        defaultMenuShouldBeFound("description.specified=true");

        // Get all the menuList where description is null
        defaultMenuShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllMenusByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where description contains DEFAULT_DESCRIPTION
        defaultMenuShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the menuList where description contains UPDATED_DESCRIPTION
        defaultMenuShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMenusByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where description does not contain DEFAULT_DESCRIPTION
        defaultMenuShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the menuList where description does not contain UPDATED_DESCRIPTION
        defaultMenuShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMenusByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where price equals to DEFAULT_PRICE
        defaultMenuShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the menuList where price equals to UPDATED_PRICE
        defaultMenuShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllMenusByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultMenuShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the menuList where price equals to UPDATED_PRICE
        defaultMenuShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllMenusByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where price is not null
        defaultMenuShouldBeFound("price.specified=true");

        // Get all the menuList where price is null
        defaultMenuShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllMenusByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where price is greater than or equal to DEFAULT_PRICE
        defaultMenuShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the menuList where price is greater than or equal to UPDATED_PRICE
        defaultMenuShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllMenusByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where price is less than or equal to DEFAULT_PRICE
        defaultMenuShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the menuList where price is less than or equal to SMALLER_PRICE
        defaultMenuShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllMenusByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where price is less than DEFAULT_PRICE
        defaultMenuShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the menuList where price is less than UPDATED_PRICE
        defaultMenuShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllMenusByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        // Get all the menuList where price is greater than DEFAULT_PRICE
        defaultMenuShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the menuList where price is greater than SMALLER_PRICE
        defaultMenuShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllMenusByRestaurantIsEqualToSomething() throws Exception {
        RestaurantModel restaurant;
        if (TestUtil.findAll(em, RestaurantModel.class).isEmpty()) {
            menuRepository.saveAndFlush(menuModel);
            restaurant = RestaurantResourceIT.createEntity(em);
        } else {
            restaurant = TestUtil.findAll(em, RestaurantModel.class).get(0);
        }
        em.persist(restaurant);
        em.flush();
        menuModel.setRestaurant(restaurant);
        menuRepository.saveAndFlush(menuModel);
        Long restaurantId = restaurant.getId();

        // Get all the menuList where restaurant equals to restaurantId
        defaultMenuShouldBeFound("restaurantId.equals=" + restaurantId);

        // Get all the menuList where restaurant equals to (restaurantId + 1)
        defaultMenuShouldNotBeFound("restaurantId.equals=" + (restaurantId + 1));
    }

    @Test
    @Transactional
    void getAllMenusByIngredientIsEqualToSomething() throws Exception {
        IngredientModel ingredient;
        if (TestUtil.findAll(em, IngredientModel.class).isEmpty()) {
            menuRepository.saveAndFlush(menuModel);
            ingredient = IngredientResourceIT.createEntity(em);
        } else {
            ingredient = TestUtil.findAll(em, IngredientModel.class).get(0);
        }
        em.persist(ingredient);
        em.flush();
        menuModel.addIngredient(ingredient);
        menuRepository.saveAndFlush(menuModel);
        Long ingredientId = ingredient.getId();

        // Get all the menuList where ingredient equals to ingredientId
        defaultMenuShouldBeFound("ingredientId.equals=" + ingredientId);

        // Get all the menuList where ingredient equals to (ingredientId + 1)
        defaultMenuShouldNotBeFound("ingredientId.equals=" + (ingredientId + 1));
    }

    @Test
    @Transactional
    void getAllMenusByTagIsEqualToSomething() throws Exception {
        TagModel tag;
        if (TestUtil.findAll(em, TagModel.class).isEmpty()) {
            menuRepository.saveAndFlush(menuModel);
            tag = TagResourceIT.createEntity(em);
        } else {
            tag = TestUtil.findAll(em, TagModel.class).get(0);
        }
        em.persist(tag);
        em.flush();
        menuModel.addTag(tag);
        menuRepository.saveAndFlush(menuModel);
        Long tagId = tag.getId();

        // Get all the menuList where tag equals to tagId
        defaultMenuShouldBeFound("tagId.equals=" + tagId);

        // Get all the menuList where tag equals to (tagId + 1)
        defaultMenuShouldNotBeFound("tagId.equals=" + (tagId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMenuShouldBeFound(String filter) throws Exception {
        restMenuMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menuModel.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())));

        // Check, that the count call also returns 1
        restMenuMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMenuShouldNotBeFound(String filter) throws Exception {
        restMenuMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMenuMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMenu() throws Exception {
        // Get the menu
        restMenuMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        int databaseSizeBeforeUpdate = menuRepository.findAll().size();

        // Update the menu
        MenuModel updatedMenuModel = menuRepository.findById(menuModel.getId()).get();
        // Disconnect from session so that the updates on updatedMenuModel are not directly saved in db
        em.detach(updatedMenuModel);
        updatedMenuModel.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).price(UPDATED_PRICE);
        MenuRepresentation menuRepresentation = menuMapper.toDto(updatedMenuModel);

        restMenuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuRepresentation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isOk());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeUpdate);
        MenuModel testMenu = menuList.get(menuList.size() - 1);
        assertThat(testMenu.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMenu.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMenu.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingMenu() throws Exception {
        int databaseSizeBeforeUpdate = menuRepository.findAll().size();
        menuModel.setId(count.incrementAndGet());

        // Create the Menu
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, menuRepresentation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMenu() throws Exception {
        int databaseSizeBeforeUpdate = menuRepository.findAll().size();
        menuModel.setId(count.incrementAndGet());

        // Create the Menu
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMenu() throws Exception {
        int databaseSizeBeforeUpdate = menuRepository.findAll().size();
        menuModel.setId(count.incrementAndGet());

        // Create the Menu
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMenuWithPatch() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        int databaseSizeBeforeUpdate = menuRepository.findAll().size();

        // Update the menu using partial update
        MenuModel partialUpdatedMenuModel = new MenuModel();
        partialUpdatedMenuModel.setId(menuModel.getId());

        restMenuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMenuModel))
            )
            .andExpect(status().isOk());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeUpdate);
        MenuModel testMenu = menuList.get(menuList.size() - 1);
        assertThat(testMenu.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMenu.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMenu.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateMenuWithPatch() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        int databaseSizeBeforeUpdate = menuRepository.findAll().size();

        // Update the menu using partial update
        MenuModel partialUpdatedMenuModel = new MenuModel();
        partialUpdatedMenuModel.setId(menuModel.getId());

        partialUpdatedMenuModel.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).price(UPDATED_PRICE);

        restMenuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMenuModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMenuModel))
            )
            .andExpect(status().isOk());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeUpdate);
        MenuModel testMenu = menuList.get(menuList.size() - 1);
        assertThat(testMenu.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMenu.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMenu.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingMenu() throws Exception {
        int databaseSizeBeforeUpdate = menuRepository.findAll().size();
        menuModel.setId(count.incrementAndGet());

        // Create the Menu
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMenuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, menuRepresentation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMenu() throws Exception {
        int databaseSizeBeforeUpdate = menuRepository.findAll().size();
        menuModel.setId(count.incrementAndGet());

        // Create the Menu
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMenu() throws Exception {
        int databaseSizeBeforeUpdate = menuRepository.findAll().size();
        menuModel.setId(count.incrementAndGet());

        // Create the Menu
        MenuRepresentation menuRepresentation = menuMapper.toDto(menuModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMenuMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(menuRepresentation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Menu in the database
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMenu() throws Exception {
        // Initialize the database
        menuRepository.saveAndFlush(menuModel);

        int databaseSizeBeforeDelete = menuRepository.findAll().size();

        // Delete the menu
        restMenuMockMvc
            .perform(delete(ENTITY_API_URL_ID, menuModel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MenuModel> menuList = menuRepository.findAll();
        assertThat(menuList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
