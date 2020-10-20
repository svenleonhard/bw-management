package de.bildwerk.bwmanagement.web.rest;

import de.bildwerk.bwmanagement.BwManagementApp;
import de.bildwerk.bwmanagement.domain.Content;
import de.bildwerk.bwmanagement.domain.Item;
import de.bildwerk.bwmanagement.repository.ContentRepository;
import de.bildwerk.bwmanagement.service.ContentService;
import de.bildwerk.bwmanagement.service.dto.ContentCriteria;
import de.bildwerk.bwmanagement.service.ContentQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ContentResource} REST controller.
 */
@SpringBootTest(classes = BwManagementApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class ContentResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentQueryService contentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContentMockMvc;

    private Content content;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Content createEntity(EntityManager em) {
        Content content = new Content()
            .description(DEFAULT_DESCRIPTION);
        return content;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Content createUpdatedEntity(EntityManager em) {
        Content content = new Content()
            .description(UPDATED_DESCRIPTION);
        return content;
    }

    @BeforeEach
    public void initTest() {
        content = createEntity(em);
    }

    @Test
    @Transactional
    public void createContent() throws Exception {
        int databaseSizeBeforeCreate = contentRepository.findAll().size();
        // Create the Content
        restContentMockMvc.perform(post("/api/contents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(content)))
            .andExpect(status().isCreated());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeCreate + 1);
        Content testContent = contentList.get(contentList.size() - 1);
        assertThat(testContent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createContentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = contentRepository.findAll().size();

        // Create the Content with an existing ID
        content.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restContentMockMvc.perform(post("/api/contents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(content)))
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = contentRepository.findAll().size();
        // set the field null
        content.setDescription(null);

        // Create the Content, which fails.


        restContentMockMvc.perform(post("/api/contents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(content)))
            .andExpect(status().isBadRequest());

        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllContents() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList
        restContentMockMvc.perform(get("/api/contents?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(content.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getContent() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get the content
        restContentMockMvc.perform(get("/api/contents/{id}", content.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(content.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }


    @Test
    @Transactional
    public void getContentsByIdFiltering() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        Long id = content.getId();

        defaultContentShouldBeFound("id.equals=" + id);
        defaultContentShouldNotBeFound("id.notEquals=" + id);

        defaultContentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultContentShouldNotBeFound("id.greaterThan=" + id);

        defaultContentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultContentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllContentsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where description equals to DEFAULT_DESCRIPTION
        defaultContentShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the contentList where description equals to UPDATED_DESCRIPTION
        defaultContentShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllContentsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where description not equals to DEFAULT_DESCRIPTION
        defaultContentShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the contentList where description not equals to UPDATED_DESCRIPTION
        defaultContentShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllContentsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultContentShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the contentList where description equals to UPDATED_DESCRIPTION
        defaultContentShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllContentsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where description is not null
        defaultContentShouldBeFound("description.specified=true");

        // Get all the contentList where description is null
        defaultContentShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllContentsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where description contains DEFAULT_DESCRIPTION
        defaultContentShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the contentList where description contains UPDATED_DESCRIPTION
        defaultContentShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllContentsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where description does not contain DEFAULT_DESCRIPTION
        defaultContentShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the contentList where description does not contain UPDATED_DESCRIPTION
        defaultContentShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllContentsByItemIsEqualToSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);
        Item item = ItemResourceIT.createEntity(em);
        em.persist(item);
        em.flush();
        content.setItem(item);
        contentRepository.saveAndFlush(content);
        Long itemId = item.getId();

        // Get all the contentList where item equals to itemId
        defaultContentShouldBeFound("itemId.equals=" + itemId);

        // Get all the contentList where item equals to itemId + 1
        defaultContentShouldNotBeFound("itemId.equals=" + (itemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultContentShouldBeFound(String filter) throws Exception {
        restContentMockMvc.perform(get("/api/contents?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(content.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restContentMockMvc.perform(get("/api/contents/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultContentShouldNotBeFound(String filter) throws Exception {
        restContentMockMvc.perform(get("/api/contents?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restContentMockMvc.perform(get("/api/contents/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingContent() throws Exception {
        // Get the content
        restContentMockMvc.perform(get("/api/contents/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateContent() throws Exception {
        // Initialize the database
        contentService.save(content);

        int databaseSizeBeforeUpdate = contentRepository.findAll().size();

        // Update the content
        Content updatedContent = contentRepository.findById(content.getId()).get();
        // Disconnect from session so that the updates on updatedContent are not directly saved in db
        em.detach(updatedContent);
        updatedContent
            .description(UPDATED_DESCRIPTION);

        restContentMockMvc.perform(put("/api/contents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedContent)))
            .andExpect(status().isOk());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
        Content testContent = contentList.get(contentList.size() - 1);
        assertThat(testContent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingContent() throws Exception {
        int databaseSizeBeforeUpdate = contentRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContentMockMvc.perform(put("/api/contents")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(content)))
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteContent() throws Exception {
        // Initialize the database
        contentService.save(content);

        int databaseSizeBeforeDelete = contentRepository.findAll().size();

        // Delete the content
        restContentMockMvc.perform(delete("/api/contents/{id}", content.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
