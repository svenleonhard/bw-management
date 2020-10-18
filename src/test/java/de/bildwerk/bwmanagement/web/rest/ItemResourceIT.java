package de.bildwerk.bwmanagement.web.rest;

import de.bildwerk.bwmanagement.BwManagementApp;
import de.bildwerk.bwmanagement.domain.Item;
import de.bildwerk.bwmanagement.domain.Image;
import de.bildwerk.bwmanagement.domain.Content;
import de.bildwerk.bwmanagement.domain.Letting;
import de.bildwerk.bwmanagement.repository.ItemRepository;
import de.bildwerk.bwmanagement.service.ItemService;
import de.bildwerk.bwmanagement.service.dto.ItemCriteria;
import de.bildwerk.bwmanagement.service.ItemQueryService;

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
 * Integration tests for the {@link ItemResource} REST controller.
 */
@SpringBootTest(classes = BwManagementApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class ItemResourceIT {

    private static final Integer DEFAULT_QR_CODE = 1;
    private static final Integer UPDATED_QR_CODE = 2;
    private static final Integer SMALLER_QR_CODE = 1 - 1;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemQueryService itemQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restItemMockMvc;

    private Item item;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Item createEntity(EntityManager em) {
        Item item = new Item()
            .qrCode(DEFAULT_QR_CODE)
            .description(DEFAULT_DESCRIPTION);
        return item;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Item createUpdatedEntity(EntityManager em) {
        Item item = new Item()
            .qrCode(UPDATED_QR_CODE)
            .description(UPDATED_DESCRIPTION);
        return item;
    }

    @BeforeEach
    public void initTest() {
        item = createEntity(em);
    }

    @Test
    @Transactional
    public void createItem() throws Exception {
        int databaseSizeBeforeCreate = itemRepository.findAll().size();
        // Create the Item
        restItemMockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isCreated());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeCreate + 1);
        Item testItem = itemList.get(itemList.size() - 1);
        assertThat(testItem.getQrCode()).isEqualTo(DEFAULT_QR_CODE);
        assertThat(testItem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = itemRepository.findAll().size();

        // Create the Item with an existing ID
        item.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restItemMockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkQrCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = itemRepository.findAll().size();
        // set the field null
        item.setQrCode(null);

        // Create the Item, which fails.


        restItemMockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = itemRepository.findAll().size();
        // set the field null
        item.setDescription(null);

        // Create the Item, which fails.


        restItemMockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllItems() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList
        restItemMockMvc.perform(get("/api/items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
            .andExpect(jsonPath("$.[*].qrCode").value(hasItem(DEFAULT_QR_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getItem() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get the item
        restItemMockMvc.perform(get("/api/items/{id}", item.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(item.getId().intValue()))
            .andExpect(jsonPath("$.qrCode").value(DEFAULT_QR_CODE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }


    @Test
    @Transactional
    public void getItemsByIdFiltering() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        Long id = item.getId();

        defaultItemShouldBeFound("id.equals=" + id);
        defaultItemShouldNotBeFound("id.notEquals=" + id);

        defaultItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultItemShouldNotBeFound("id.greaterThan=" + id);

        defaultItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultItemShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllItemsByQrCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where qrCode equals to DEFAULT_QR_CODE
        defaultItemShouldBeFound("qrCode.equals=" + DEFAULT_QR_CODE);

        // Get all the itemList where qrCode equals to UPDATED_QR_CODE
        defaultItemShouldNotBeFound("qrCode.equals=" + UPDATED_QR_CODE);
    }

    @Test
    @Transactional
    public void getAllItemsByQrCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where qrCode not equals to DEFAULT_QR_CODE
        defaultItemShouldNotBeFound("qrCode.notEquals=" + DEFAULT_QR_CODE);

        // Get all the itemList where qrCode not equals to UPDATED_QR_CODE
        defaultItemShouldBeFound("qrCode.notEquals=" + UPDATED_QR_CODE);
    }

    @Test
    @Transactional
    public void getAllItemsByQrCodeIsInShouldWork() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where qrCode in DEFAULT_QR_CODE or UPDATED_QR_CODE
        defaultItemShouldBeFound("qrCode.in=" + DEFAULT_QR_CODE + "," + UPDATED_QR_CODE);

        // Get all the itemList where qrCode equals to UPDATED_QR_CODE
        defaultItemShouldNotBeFound("qrCode.in=" + UPDATED_QR_CODE);
    }

    @Test
    @Transactional
    public void getAllItemsByQrCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where qrCode is not null
        defaultItemShouldBeFound("qrCode.specified=true");

        // Get all the itemList where qrCode is null
        defaultItemShouldNotBeFound("qrCode.specified=false");
    }

    @Test
    @Transactional
    public void getAllItemsByQrCodeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where qrCode is greater than or equal to DEFAULT_QR_CODE
        defaultItemShouldBeFound("qrCode.greaterThanOrEqual=" + DEFAULT_QR_CODE);

        // Get all the itemList where qrCode is greater than or equal to UPDATED_QR_CODE
        defaultItemShouldNotBeFound("qrCode.greaterThanOrEqual=" + UPDATED_QR_CODE);
    }

    @Test
    @Transactional
    public void getAllItemsByQrCodeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where qrCode is less than or equal to DEFAULT_QR_CODE
        defaultItemShouldBeFound("qrCode.lessThanOrEqual=" + DEFAULT_QR_CODE);

        // Get all the itemList where qrCode is less than or equal to SMALLER_QR_CODE
        defaultItemShouldNotBeFound("qrCode.lessThanOrEqual=" + SMALLER_QR_CODE);
    }

    @Test
    @Transactional
    public void getAllItemsByQrCodeIsLessThanSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where qrCode is less than DEFAULT_QR_CODE
        defaultItemShouldNotBeFound("qrCode.lessThan=" + DEFAULT_QR_CODE);

        // Get all the itemList where qrCode is less than UPDATED_QR_CODE
        defaultItemShouldBeFound("qrCode.lessThan=" + UPDATED_QR_CODE);
    }

    @Test
    @Transactional
    public void getAllItemsByQrCodeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where qrCode is greater than DEFAULT_QR_CODE
        defaultItemShouldNotBeFound("qrCode.greaterThan=" + DEFAULT_QR_CODE);

        // Get all the itemList where qrCode is greater than SMALLER_QR_CODE
        defaultItemShouldBeFound("qrCode.greaterThan=" + SMALLER_QR_CODE);
    }


    @Test
    @Transactional
    public void getAllItemsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where description equals to DEFAULT_DESCRIPTION
        defaultItemShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the itemList where description equals to UPDATED_DESCRIPTION
        defaultItemShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllItemsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where description not equals to DEFAULT_DESCRIPTION
        defaultItemShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the itemList where description not equals to UPDATED_DESCRIPTION
        defaultItemShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllItemsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultItemShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the itemList where description equals to UPDATED_DESCRIPTION
        defaultItemShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllItemsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where description is not null
        defaultItemShouldBeFound("description.specified=true");

        // Get all the itemList where description is null
        defaultItemShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllItemsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where description contains DEFAULT_DESCRIPTION
        defaultItemShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the itemList where description contains UPDATED_DESCRIPTION
        defaultItemShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllItemsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);

        // Get all the itemList where description does not contain DEFAULT_DESCRIPTION
        defaultItemShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the itemList where description does not contain UPDATED_DESCRIPTION
        defaultItemShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllItemsByPictureIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);
        Image picture = ImageResourceIT.createEntity(em);
        em.persist(picture);
        em.flush();
        item.setPicture(picture);
        itemRepository.saveAndFlush(item);
        Long pictureId = picture.getId();

        // Get all the itemList where picture equals to pictureId
        defaultItemShouldBeFound("pictureId.equals=" + pictureId);

        // Get all the itemList where picture equals to pictureId + 1
        defaultItemShouldNotBeFound("pictureId.equals=" + (pictureId + 1));
    }


    @Test
    @Transactional
    public void getAllItemsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);
        Content content = ContentResourceIT.createEntity(em);
        em.persist(content);
        em.flush();
        item.addContent(content);
        itemRepository.saveAndFlush(item);
        Long contentId = content.getId();

        // Get all the itemList where content equals to contentId
        defaultItemShouldBeFound("contentId.equals=" + contentId);

        // Get all the itemList where content equals to contentId + 1
        defaultItemShouldNotBeFound("contentId.equals=" + (contentId + 1));
    }


    @Test
    @Transactional
    public void getAllItemsByLettingIsEqualToSomething() throws Exception {
        // Initialize the database
        itemRepository.saveAndFlush(item);
        Letting letting = LettingResourceIT.createEntity(em);
        em.persist(letting);
        em.flush();
        item.addLetting(letting);
        itemRepository.saveAndFlush(item);
        Long lettingId = letting.getId();

        // Get all the itemList where letting equals to lettingId
        defaultItemShouldBeFound("lettingId.equals=" + lettingId);

        // Get all the itemList where letting equals to lettingId + 1
        defaultItemShouldNotBeFound("lettingId.equals=" + (lettingId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultItemShouldBeFound(String filter) throws Exception {
        restItemMockMvc.perform(get("/api/items?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(item.getId().intValue())))
            .andExpect(jsonPath("$.[*].qrCode").value(hasItem(DEFAULT_QR_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restItemMockMvc.perform(get("/api/items/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultItemShouldNotBeFound(String filter) throws Exception {
        restItemMockMvc.perform(get("/api/items?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restItemMockMvc.perform(get("/api/items/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingItem() throws Exception {
        // Get the item
        restItemMockMvc.perform(get("/api/items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateItem() throws Exception {
        // Initialize the database
        itemService.save(item);

        int databaseSizeBeforeUpdate = itemRepository.findAll().size();

        // Update the item
        Item updatedItem = itemRepository.findById(item.getId()).get();
        // Disconnect from session so that the updates on updatedItem are not directly saved in db
        em.detach(updatedItem);
        updatedItem
            .qrCode(UPDATED_QR_CODE)
            .description(UPDATED_DESCRIPTION);

        restItemMockMvc.perform(put("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedItem)))
            .andExpect(status().isOk());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
        Item testItem = itemList.get(itemList.size() - 1);
        assertThat(testItem.getQrCode()).isEqualTo(UPDATED_QR_CODE);
        assertThat(testItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingItem() throws Exception {
        int databaseSizeBeforeUpdate = itemRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restItemMockMvc.perform(put("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(item)))
            .andExpect(status().isBadRequest());

        // Validate the Item in the database
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteItem() throws Exception {
        // Initialize the database
        itemService.save(item);

        int databaseSizeBeforeDelete = itemRepository.findAll().size();

        // Delete the item
        restItemMockMvc.perform(delete("/api/items/{id}", item.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
