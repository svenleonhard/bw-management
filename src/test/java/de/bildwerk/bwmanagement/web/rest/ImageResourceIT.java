package de.bildwerk.bwmanagement.web.rest;

import de.bildwerk.bwmanagement.BwManagementApp;
import de.bildwerk.bwmanagement.domain.Image;
import de.bildwerk.bwmanagement.repository.ImageRepository;
import de.bildwerk.bwmanagement.service.ImageService;
import de.bildwerk.bwmanagement.service.dto.ImageCriteria;
import de.bildwerk.bwmanagement.service.ImageQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ImageResource} REST controller.
 */
@SpringBootTest(classes = BwManagementApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class ImageResourceIT {

    private static final byte[] DEFAULT_DATA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DATA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DATA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DATA_CONTENT_TYPE = "image/png";

    private static final LocalDate DEFAULT_UPLOAD_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPLOAD_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_UPLOAD_DATE = LocalDate.ofEpochDay(-1L);

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageQueryService imageQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImageMockMvc;

    private Image image;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createEntity(EntityManager em) {
        Image image = new Image()
            .data(DEFAULT_DATA)
            .dataContentType(DEFAULT_DATA_CONTENT_TYPE)
            .uploadDate(DEFAULT_UPLOAD_DATE);
        return image;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createUpdatedEntity(EntityManager em) {
        Image image = new Image()
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .uploadDate(UPDATED_UPLOAD_DATE);
        return image;
    }

    @BeforeEach
    public void initTest() {
        image = createEntity(em);
    }

    @Test
    @Transactional
    public void createImage() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().size();
        // Create the Image
        restImageMockMvc.perform(post("/api/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(image)))
            .andExpect(status().isCreated());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate + 1);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testImage.getDataContentType()).isEqualTo(DEFAULT_DATA_CONTENT_TYPE);
        assertThat(testImage.getUploadDate()).isEqualTo(DEFAULT_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void createImageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().size();

        // Create the Image with an existing ID
        image.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restImageMockMvc.perform(post("/api/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(image)))
            .andExpect(status().isBadRequest());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllImages() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList
        restImageMockMvc.perform(get("/api/images?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataContentType").value(hasItem(DEFAULT_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA))))
            .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(DEFAULT_UPLOAD_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get the image
        restImageMockMvc.perform(get("/api/images/{id}", image.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(image.getId().intValue()))
            .andExpect(jsonPath("$.dataContentType").value(DEFAULT_DATA_CONTENT_TYPE))
            .andExpect(jsonPath("$.data").value(Base64Utils.encodeToString(DEFAULT_DATA)))
            .andExpect(jsonPath("$.uploadDate").value(DEFAULT_UPLOAD_DATE.toString()));
    }


    @Test
    @Transactional
    public void getImagesByIdFiltering() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        Long id = image.getId();

        defaultImageShouldBeFound("id.equals=" + id);
        defaultImageShouldNotBeFound("id.notEquals=" + id);

        defaultImageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultImageShouldNotBeFound("id.greaterThan=" + id);

        defaultImageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultImageShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllImagesByUploadDateIsEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where uploadDate equals to DEFAULT_UPLOAD_DATE
        defaultImageShouldBeFound("uploadDate.equals=" + DEFAULT_UPLOAD_DATE);

        // Get all the imageList where uploadDate equals to UPDATED_UPLOAD_DATE
        defaultImageShouldNotBeFound("uploadDate.equals=" + UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void getAllImagesByUploadDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where uploadDate not equals to DEFAULT_UPLOAD_DATE
        defaultImageShouldNotBeFound("uploadDate.notEquals=" + DEFAULT_UPLOAD_DATE);

        // Get all the imageList where uploadDate not equals to UPDATED_UPLOAD_DATE
        defaultImageShouldBeFound("uploadDate.notEquals=" + UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void getAllImagesByUploadDateIsInShouldWork() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where uploadDate in DEFAULT_UPLOAD_DATE or UPDATED_UPLOAD_DATE
        defaultImageShouldBeFound("uploadDate.in=" + DEFAULT_UPLOAD_DATE + "," + UPDATED_UPLOAD_DATE);

        // Get all the imageList where uploadDate equals to UPDATED_UPLOAD_DATE
        defaultImageShouldNotBeFound("uploadDate.in=" + UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void getAllImagesByUploadDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where uploadDate is not null
        defaultImageShouldBeFound("uploadDate.specified=true");

        // Get all the imageList where uploadDate is null
        defaultImageShouldNotBeFound("uploadDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllImagesByUploadDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where uploadDate is greater than or equal to DEFAULT_UPLOAD_DATE
        defaultImageShouldBeFound("uploadDate.greaterThanOrEqual=" + DEFAULT_UPLOAD_DATE);

        // Get all the imageList where uploadDate is greater than or equal to UPDATED_UPLOAD_DATE
        defaultImageShouldNotBeFound("uploadDate.greaterThanOrEqual=" + UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void getAllImagesByUploadDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where uploadDate is less than or equal to DEFAULT_UPLOAD_DATE
        defaultImageShouldBeFound("uploadDate.lessThanOrEqual=" + DEFAULT_UPLOAD_DATE);

        // Get all the imageList where uploadDate is less than or equal to SMALLER_UPLOAD_DATE
        defaultImageShouldNotBeFound("uploadDate.lessThanOrEqual=" + SMALLER_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void getAllImagesByUploadDateIsLessThanSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where uploadDate is less than DEFAULT_UPLOAD_DATE
        defaultImageShouldNotBeFound("uploadDate.lessThan=" + DEFAULT_UPLOAD_DATE);

        // Get all the imageList where uploadDate is less than UPDATED_UPLOAD_DATE
        defaultImageShouldBeFound("uploadDate.lessThan=" + UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void getAllImagesByUploadDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList where uploadDate is greater than DEFAULT_UPLOAD_DATE
        defaultImageShouldNotBeFound("uploadDate.greaterThan=" + DEFAULT_UPLOAD_DATE);

        // Get all the imageList where uploadDate is greater than SMALLER_UPLOAD_DATE
        defaultImageShouldBeFound("uploadDate.greaterThan=" + SMALLER_UPLOAD_DATE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultImageShouldBeFound(String filter) throws Exception {
        restImageMockMvc.perform(get("/api/images?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataContentType").value(hasItem(DEFAULT_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA))))
            .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(DEFAULT_UPLOAD_DATE.toString())));

        // Check, that the count call also returns 1
        restImageMockMvc.perform(get("/api/images/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultImageShouldNotBeFound(String filter) throws Exception {
        restImageMockMvc.perform(get("/api/images?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restImageMockMvc.perform(get("/api/images/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingImage() throws Exception {
        // Get the image
        restImageMockMvc.perform(get("/api/images/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImage() throws Exception {
        // Initialize the database
        imageService.save(image);

        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // Update the image
        Image updatedImage = imageRepository.findById(image.getId()).get();
        // Disconnect from session so that the updates on updatedImage are not directly saved in db
        em.detach(updatedImage);
        updatedImage
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .uploadDate(UPDATED_UPLOAD_DATE);

        restImageMockMvc.perform(put("/api/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedImage)))
            .andExpect(status().isOk());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testImage.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
        assertThat(testImage.getUploadDate()).isEqualTo(UPDATED_UPLOAD_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImageMockMvc.perform(put("/api/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(image)))
            .andExpect(status().isBadRequest());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteImage() throws Exception {
        // Initialize the database
        imageService.save(image);

        int databaseSizeBeforeDelete = imageRepository.findAll().size();

        // Delete the image
        restImageMockMvc.perform(delete("/api/images/{id}", image.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
