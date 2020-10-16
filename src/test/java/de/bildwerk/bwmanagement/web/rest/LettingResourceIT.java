package de.bildwerk.bwmanagement.web.rest;

import de.bildwerk.bwmanagement.BwManagementApp;
import de.bildwerk.bwmanagement.domain.Letting;
import de.bildwerk.bwmanagement.repository.LettingRepository;

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

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    @Autowired
    private LettingRepository lettingRepository;

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
    public void getNonExistingLetting() throws Exception {
        // Get the letting
        restLettingMockMvc.perform(get("/api/lettings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLetting() throws Exception {
        // Initialize the database
        lettingRepository.saveAndFlush(letting);

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
        lettingRepository.saveAndFlush(letting);

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
