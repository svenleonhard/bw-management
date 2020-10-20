package de.bildwerk.bwmanagement.web.rest;

import de.bildwerk.bwmanagement.BwManagementApp;
import de.bildwerk.bwmanagement.domain.Letting;
import de.bildwerk.bwmanagement.domain.Item;
import de.bildwerk.bwmanagement.repository.LettingRepository;
import de.bildwerk.bwmanagement.service.LettingService;
import de.bildwerk.bwmanagement.service.dto.LettingCriteria;
import de.bildwerk.bwmanagement.service.LettingQueryService;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link LettingResource} REST controller.
 */
@SpringBootTest(classes = BwManagementApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class LettingResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    @Autowired
    private LettingRepository lettingRepository;

    @Autowired
    private LettingService lettingService;

    @Autowired
    private LettingQueryService lettingQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLettingMockMvc;

    private Letting letting;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Letting createEntity(EntityManager em) {
        Letting letting = new Letting()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .name(DEFAULT_NAME)
            .location(DEFAULT_LOCATION);
        return letting;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Letting createUpdatedEntity(EntityManager em) {
        Letting letting = new Letting()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .name(UPDATED_NAME)
            .location(UPDATED_LOCATION);
        return letting;
    }

    @BeforeEach
    public void initTest() {
        letting = createEntity(em);
    }

    @Test
    @Transactional
    public void createLetting() throws Exception {
        int databaseSizeBeforeCreate = lettingRepository.findAll().size();
        // Create the Letting
        restLettingMockMvc.perform(post("/api/lettings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(letting)))
            .andExpect(status().isCreated());

        // Validate the Letting in the database
        List<Letting> lettingList = lettingRepository.findAll();
        assertThat(lettingList).hasSize(databaseSizeBeforeCreate + 1);
        Letting testLetting = lettingList.get(lettingList.size() - 1);
        assertThat(testLetting.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testLetting.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testLetting.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLetting.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    @Transactional
    public void createLettingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = lettingRepository.findAll().size();

        // Create the Letting with an existing ID
        letting.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLettingMockMvc.perform(post("/api/lettings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(letting)))
            .andExpect(status().isBadRequest());

        // Validate the Letting in the database
        List<Letting> lettingList = lettingRepository.findAll();
        assertThat(lettingList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = lettingRepository.findAll().size();
        // set the field null
        letting.setStartDate(null);

        // Create the Letting, which fails.


        restLettingMockMvc.perform(post("/api/lettings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(letting)))
            .andExpect(status().isBadRequest());

        List<Letting> lettingList = lettingRepository.findAll();
        assertThat(lettingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLettings() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList
        restLettingMockMvc.perform(get("/api/lettings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(letting.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)));
    }
    
    @Test
    @Transactional
    public void getLetting() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get the letting
        restLettingMockMvc.perform(get("/api/lettings/{id}", letting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(letting.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION));
    }


    @Test
    @Transactional
    public void getLettingsByIdFiltering() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        Long id = letting.getId();

        defaultLettingShouldBeFound("id.equals=" + id);
        defaultLettingShouldNotBeFound("id.notEquals=" + id);

        defaultLettingShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLettingShouldNotBeFound("id.greaterThan=" + id);

        defaultLettingShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLettingShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllLettingsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where startDate equals to DEFAULT_START_DATE
        defaultLettingShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the lettingList where startDate equals to UPDATED_START_DATE
        defaultLettingShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where startDate not equals to DEFAULT_START_DATE
        defaultLettingShouldNotBeFound("startDate.notEquals=" + DEFAULT_START_DATE);

        // Get all the lettingList where startDate not equals to UPDATED_START_DATE
        defaultLettingShouldBeFound("startDate.notEquals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultLettingShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the lettingList where startDate equals to UPDATED_START_DATE
        defaultLettingShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where startDate is not null
        defaultLettingShouldBeFound("startDate.specified=true");

        // Get all the lettingList where startDate is null
        defaultLettingShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllLettingsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where startDate is greater than or equal to DEFAULT_START_DATE
        defaultLettingShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the lettingList where startDate is greater than or equal to UPDATED_START_DATE
        defaultLettingShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where startDate is less than or equal to DEFAULT_START_DATE
        defaultLettingShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the lettingList where startDate is less than or equal to SMALLER_START_DATE
        defaultLettingShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where startDate is less than DEFAULT_START_DATE
        defaultLettingShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the lettingList where startDate is less than UPDATED_START_DATE
        defaultLettingShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where startDate is greater than DEFAULT_START_DATE
        defaultLettingShouldNotBeFound("startDate.greaterThan=" + DEFAULT_START_DATE);

        // Get all the lettingList where startDate is greater than SMALLER_START_DATE
        defaultLettingShouldBeFound("startDate.greaterThan=" + SMALLER_START_DATE);
    }


    @Test
    @Transactional
    public void getAllLettingsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where endDate equals to DEFAULT_END_DATE
        defaultLettingShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the lettingList where endDate equals to UPDATED_END_DATE
        defaultLettingShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where endDate not equals to DEFAULT_END_DATE
        defaultLettingShouldNotBeFound("endDate.notEquals=" + DEFAULT_END_DATE);

        // Get all the lettingList where endDate not equals to UPDATED_END_DATE
        defaultLettingShouldBeFound("endDate.notEquals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultLettingShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the lettingList where endDate equals to UPDATED_END_DATE
        defaultLettingShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where endDate is not null
        defaultLettingShouldBeFound("endDate.specified=true");

        // Get all the lettingList where endDate is null
        defaultLettingShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllLettingsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where endDate is greater than or equal to DEFAULT_END_DATE
        defaultLettingShouldBeFound("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the lettingList where endDate is greater than or equal to UPDATED_END_DATE
        defaultLettingShouldNotBeFound("endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where endDate is less than or equal to DEFAULT_END_DATE
        defaultLettingShouldBeFound("endDate.lessThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the lettingList where endDate is less than or equal to SMALLER_END_DATE
        defaultLettingShouldNotBeFound("endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where endDate is less than DEFAULT_END_DATE
        defaultLettingShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the lettingList where endDate is less than UPDATED_END_DATE
        defaultLettingShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllLettingsByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where endDate is greater than DEFAULT_END_DATE
        defaultLettingShouldNotBeFound("endDate.greaterThan=" + DEFAULT_END_DATE);

        // Get all the lettingList where endDate is greater than SMALLER_END_DATE
        defaultLettingShouldBeFound("endDate.greaterThan=" + SMALLER_END_DATE);
    }


    @Test
    @Transactional
    public void getAllLettingsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where name equals to DEFAULT_NAME
        defaultLettingShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the lettingList where name equals to UPDATED_NAME
        defaultLettingShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLettingsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where name not equals to DEFAULT_NAME
        defaultLettingShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the lettingList where name not equals to UPDATED_NAME
        defaultLettingShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLettingsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where name in DEFAULT_NAME or UPDATED_NAME
        defaultLettingShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the lettingList where name equals to UPDATED_NAME
        defaultLettingShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLettingsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where name is not null
        defaultLettingShouldBeFound("name.specified=true");

        // Get all the lettingList where name is null
        defaultLettingShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllLettingsByNameContainsSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where name contains DEFAULT_NAME
        defaultLettingShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the lettingList where name contains UPDATED_NAME
        defaultLettingShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLettingsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where name does not contain DEFAULT_NAME
        defaultLettingShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the lettingList where name does not contain UPDATED_NAME
        defaultLettingShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllLettingsByLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where location equals to DEFAULT_LOCATION
        defaultLettingShouldBeFound("location.equals=" + DEFAULT_LOCATION);

        // Get all the lettingList where location equals to UPDATED_LOCATION
        defaultLettingShouldNotBeFound("location.equals=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    public void getAllLettingsByLocationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where location not equals to DEFAULT_LOCATION
        defaultLettingShouldNotBeFound("location.notEquals=" + DEFAULT_LOCATION);

        // Get all the lettingList where location not equals to UPDATED_LOCATION
        defaultLettingShouldBeFound("location.notEquals=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    public void getAllLettingsByLocationIsInShouldWork() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where location in DEFAULT_LOCATION or UPDATED_LOCATION
        defaultLettingShouldBeFound("location.in=" + DEFAULT_LOCATION + "," + UPDATED_LOCATION);

        // Get all the lettingList where location equals to UPDATED_LOCATION
        defaultLettingShouldNotBeFound("location.in=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    public void getAllLettingsByLocationIsNullOrNotNull() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where location is not null
        defaultLettingShouldBeFound("location.specified=true");

        // Get all the lettingList where location is null
        defaultLettingShouldNotBeFound("location.specified=false");
    }
                @Test
    @Transactional
    public void getAllLettingsByLocationContainsSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where location contains DEFAULT_LOCATION
        defaultLettingShouldBeFound("location.contains=" + DEFAULT_LOCATION);

        // Get all the lettingList where location contains UPDATED_LOCATION
        defaultLettingShouldNotBeFound("location.contains=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    public void getAllLettingsByLocationNotContainsSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

        // Get all the lettingList where location does not contain DEFAULT_LOCATION
        defaultLettingShouldNotBeFound("location.doesNotContain=" + DEFAULT_LOCATION);

        // Get all the lettingList where location does not contain UPDATED_LOCATION
        defaultLettingShouldBeFound("location.doesNotContain=" + UPDATED_LOCATION);
    }


    @Test
    @Transactional
    public void getAllLettingsByItemIsEqualToSomething() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);
        Item item = ItemResourceIT.createEntity(em);
        em.persist(item);
        em.flush();
        letting.setItem(item);
        lettingRepository.saveAndFlush(letting);
        Long itemId = item.getId();

        // Get all the lettingList where item equals to itemId
        defaultLettingShouldBeFound("itemId.equals=" + itemId);

        // Get all the lettingList where item equals to itemId + 1
        defaultLettingShouldNotBeFound("itemId.equals=" + (itemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLettingShouldBeFound(String filter) throws Exception {
        restLettingMockMvc.perform(get("/api/lettings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(letting.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)));

        // Check, that the count call also returns 1
        restLettingMockMvc.perform(get("/api/lettings/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLettingShouldNotBeFound(String filter) throws Exception {
        restLettingMockMvc.perform(get("/api/lettings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLettingMockMvc.perform(get("/api/lettings/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingLetting() throws Exception {
        // Get the letting
        restLettingMockMvc.perform(get("/api/lettings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLetting() throws Exception {
        // Initialize the database
        lettingService.save(letting);

        int databaseSizeBeforeUpdate = lettingRepository.findAll().size();

        // Update the letting
        Letting updatedLetting = lettingRepository.findById(letting.getId()).get();
        // Disconnect from session so that the updates on updatedLetting are not directly saved in db
        em.detach(updatedLetting);
        updatedLetting
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .name(UPDATED_NAME)
            .location(UPDATED_LOCATION);

        restLettingMockMvc.perform(put("/api/lettings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLetting)))
            .andExpect(status().isOk());

        // Validate the Letting in the database
        List<Letting> lettingList = lettingRepository.findAll();
        assertThat(lettingList).hasSize(databaseSizeBeforeUpdate);
        Letting testLetting = lettingList.get(lettingList.size() - 1);
        assertThat(testLetting.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testLetting.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testLetting.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLetting.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    @Transactional
    public void updateNonExistingLetting() throws Exception {
        int databaseSizeBeforeUpdate = lettingRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLettingMockMvc.perform(put("/api/lettings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(letting)))
            .andExpect(status().isBadRequest());

        // Validate the Letting in the database
        List<Letting> lettingList = lettingRepository.findAll();
        assertThat(lettingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLetting() throws Exception {
        // Initialize the database
        lettingService.save(letting);

        int databaseSizeBeforeDelete = lettingRepository.findAll().size();

        // Delete the letting
        restLettingMockMvc.perform(delete("/api/lettings/{id}", letting.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Letting> lettingList = lettingRepository.findAll();
        assertThat(lettingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
