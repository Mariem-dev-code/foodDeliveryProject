package com.food.delivery.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.food.delivery.IntegrationTest;
import com.food.delivery.domain.MenuModel;
import com.food.delivery.domain.TagModel;
import com.food.delivery.domain.enumeration.Tag;
import com.food.delivery.repository.TagRepository;
import com.food.delivery.service.criteria.TagCriteria;
import com.food.delivery.service.dto.TagRepresentation;
import com.food.delivery.service.mapper.TagMapper;
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
 * Integration tests for the {@link TagResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TagResourceIT {

    private static final Tag DEFAULT_NAME = Tag.POPULAR;
    private static final Tag UPDATED_NAME = Tag.NEW;

    private static final String ENTITY_API_URL = "/api/tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTagMockMvc;

    private TagModel tagModel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagModel createEntity(EntityManager em) {
        TagModel tagModel = new TagModel().name(DEFAULT_NAME);
        return tagModel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagModel createUpdatedEntity(EntityManager em) {
        TagModel tagModel = new TagModel().name(UPDATED_NAME);
        return tagModel;
    }

    @BeforeEach
    public void initTest() {
        tagModel = createEntity(em);
    }

    @Test
    @Transactional
    void createTag() throws Exception {
        int databaseSizeBeforeCreate = tagRepository.findAll().size();
        // Create the Tag
        TagRepresentation tagRepresentation = tagMapper.toDto(tagModel);
        restTagMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tagRepresentation))
            )
            .andExpect(status().isCreated());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate + 1);
        TagModel testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createTagWithExistingId() throws Exception {
        // Create the Tag with an existing ID
        tagModel.setId(1L);
        TagRepresentation tagRepresentation = tagMapper.toDto(tagModel);

        int databaseSizeBeforeCreate = tagRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTagMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tagRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTags() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        // Get all the tagList
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagModel.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    void getTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        // Get the tag
        restTagMockMvc
            .perform(get(ENTITY_API_URL_ID, tagModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tagModel.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    void getTagsByIdFiltering() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        Long id = tagModel.getId();

        defaultTagShouldBeFound("id.equals=" + id);
        defaultTagShouldNotBeFound("id.notEquals=" + id);

        defaultTagShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTagShouldNotBeFound("id.greaterThan=" + id);

        defaultTagShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTagShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTagsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        // Get all the tagList where name equals to DEFAULT_NAME
        defaultTagShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the tagList where name equals to UPDATED_NAME
        defaultTagShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTagsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        // Get all the tagList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTagShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the tagList where name equals to UPDATED_NAME
        defaultTagShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTagsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        // Get all the tagList where name is not null
        defaultTagShouldBeFound("name.specified=true");

        // Get all the tagList where name is null
        defaultTagShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTagsByMenuIsEqualToSomething() throws Exception {
        MenuModel menu;
        if (TestUtil.findAll(em, MenuModel.class).isEmpty()) {
            tagRepository.saveAndFlush(tagModel);
            menu = MenuResourceIT.createEntity(em);
        } else {
            menu = TestUtil.findAll(em, MenuModel.class).get(0);
        }
        em.persist(menu);
        em.flush();
        tagModel.addMenu(menu);
        tagRepository.saveAndFlush(tagModel);
        Long menuId = menu.getId();

        // Get all the tagList where menu equals to menuId
        defaultTagShouldBeFound("menuId.equals=" + menuId);

        // Get all the tagList where menu equals to (menuId + 1)
        defaultTagShouldNotBeFound("menuId.equals=" + (menuId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTagShouldBeFound(String filter) throws Exception {
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagModel.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));

        // Check, that the count call also returns 1
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTagShouldNotBeFound(String filter) throws Exception {
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTag() throws Exception {
        // Get the tag
        restTagMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // Update the tag
        TagModel updatedTagModel = tagRepository.findById(tagModel.getId()).get();
        // Disconnect from session so that the updates on updatedTagModel are not directly saved in db
        em.detach(updatedTagModel);
        updatedTagModel.name(UPDATED_NAME);
        TagRepresentation tagRepresentation = tagMapper.toDto(updatedTagModel);

        restTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tagRepresentation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagRepresentation))
            )
            .andExpect(status().isOk());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
        TagModel testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();
        tagModel.setId(count.incrementAndGet());

        // Create the Tag
        TagRepresentation tagRepresentation = tagMapper.toDto(tagModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tagRepresentation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();
        tagModel.setId(count.incrementAndGet());

        // Create the Tag
        TagRepresentation tagRepresentation = tagMapper.toDto(tagModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tagRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();
        tagModel.setId(count.incrementAndGet());

        // Create the Tag
        TagRepresentation tagRepresentation = tagMapper.toDto(tagModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tagRepresentation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTagWithPatch() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // Update the tag using partial update
        TagModel partialUpdatedTagModel = new TagModel();
        partialUpdatedTagModel.setId(tagModel.getId());

        partialUpdatedTagModel.name(UPDATED_NAME);

        restTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTagModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTagModel))
            )
            .andExpect(status().isOk());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
        TagModel testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateTagWithPatch() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // Update the tag using partial update
        TagModel partialUpdatedTagModel = new TagModel();
        partialUpdatedTagModel.setId(tagModel.getId());

        partialUpdatedTagModel.name(UPDATED_NAME);

        restTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTagModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTagModel))
            )
            .andExpect(status().isOk());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
        TagModel testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();
        tagModel.setId(count.incrementAndGet());

        // Create the Tag
        TagRepresentation tagRepresentation = tagMapper.toDto(tagModel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tagRepresentation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tagRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();
        tagModel.setId(count.incrementAndGet());

        // Create the Tag
        TagRepresentation tagRepresentation = tagMapper.toDto(tagModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tagRepresentation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();
        tagModel.setId(count.incrementAndGet());

        // Create the Tag
        TagRepresentation tagRepresentation = tagMapper.toDto(tagModel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tagRepresentation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tag in the database
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tagModel);

        int databaseSizeBeforeDelete = tagRepository.findAll().size();

        // Delete the tag
        restTagMockMvc
            .perform(delete(ENTITY_API_URL_ID, tagModel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TagModel> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
