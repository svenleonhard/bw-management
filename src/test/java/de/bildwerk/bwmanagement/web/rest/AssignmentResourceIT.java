package de.bildwerk.bwmanagement.web.rest;

import de.bildwerk.bwmanagement.BwManagementApp;
import de.bildwerk.bwmanagement.domain.Assignment;
import de.bildwerk.bwmanagement.domain.Item;
import de.bildwerk.bwmanagement.repository.AssignmentRepository;
import de.bildwerk.bwmanagement.service.AssignmentService;
import de.bildwerk.bwmanagement.service.dto.AssignmentCriteria;
import de.bildwerk.bwmanagement.service.AssignmentQueryService;

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
 * Integration tests for the {@link AssignmentResource} REST controller.
 */
@SpringBootTest(classes = BwManagementApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class AssignmentResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentQueryService assignmentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssignmentMockMvc;

    private Assignment assignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assignment createEntity(EntityManager em) {
        Assignment assignment = new Assignment()
            .description(DEFAULT_DESCRIPTION)
            .comment(DEFAULT_COMMENT);
        return assignment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assignment createUpdatedEntity(EntityManager em) {
        Assignment assignment = new Assignment()
            .description(UPDATED_DESCRIPTION)
            .comment(UPDATED_COMMENT);
        return assignment;
    }

    @BeforeEach
    public void initTest() {
        assignment = createEntity(em);
    }

    @Test
    @Transactional
    public void createAssignment() throws Exception {
        int databaseSizeBeforeCreate = assignmentRepository.findAll().size();
        // Create the Assignment
        restAssignmentMockMvc.perform(post("/api/assignments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(assignment)))
            .andExpect(status().isCreated());

        // Validate the Assignment in the database
        List<Assignment> assignmentList = assignmentRepository.findAll();
        assertThat(assignmentList).hasSize(databaseSizeBeforeCreate + 1);
        Assignment testAssignment = assignmentList.get(assignmentList.size() - 1);
        assertThat(testAssignment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAssignment.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    public void createAssignmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = assignmentRepository.findAll().size();

        // Create the Assignment with an existing ID
        assignment.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssignmentMockMvc.perform(post("/api/assignments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(assignment)))
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        List<Assignment> assignmentList = assignmentRepository.findAll();
        assertThat(assignmentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllAssignments() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList
        restAssignmentMockMvc.perform(get("/api/assignments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));
    }
    
    @Test
    @Transactional
    public void getAssignment() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get the assignment
        restAssignmentMockMvc.perform(get("/api/assignments/{id}", assignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assignment.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT));
    }


    @Test
    @Transactional
    public void getAssignmentsByIdFiltering() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        Long id = assignment.getId();

        defaultAssignmentShouldBeFound("id.equals=" + id);
        defaultAssignmentShouldNotBeFound("id.notEquals=" + id);

        defaultAssignmentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAssignmentShouldNotBeFound("id.greaterThan=" + id);

        defaultAssignmentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAssignmentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllAssignmentsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where description equals to DEFAULT_DESCRIPTION
        defaultAssignmentShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the assignmentList where description equals to UPDATED_DESCRIPTION
        defaultAssignmentShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAssignmentsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where description not equals to DEFAULT_DESCRIPTION
        defaultAssignmentShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the assignmentList where description not equals to UPDATED_DESCRIPTION
        defaultAssignmentShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAssignmentsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultAssignmentShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the assignmentList where description equals to UPDATED_DESCRIPTION
        defaultAssignmentShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAssignmentsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where description is not null
        defaultAssignmentShouldBeFound("description.specified=true");

        // Get all the assignmentList where description is null
        defaultAssignmentShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllAssignmentsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where description contains DEFAULT_DESCRIPTION
        defaultAssignmentShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the assignmentList where description contains UPDATED_DESCRIPTION
        defaultAssignmentShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAssignmentsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where description does not contain DEFAULT_DESCRIPTION
        defaultAssignmentShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the assignmentList where description does not contain UPDATED_DESCRIPTION
        defaultAssignmentShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllAssignmentsByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where comment equals to DEFAULT_COMMENT
        defaultAssignmentShouldBeFound("comment.equals=" + DEFAULT_COMMENT);

        // Get all the assignmentList where comment equals to UPDATED_COMMENT
        defaultAssignmentShouldNotBeFound("comment.equals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void getAllAssignmentsByCommentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where comment not equals to DEFAULT_COMMENT
        defaultAssignmentShouldNotBeFound("comment.notEquals=" + DEFAULT_COMMENT);

        // Get all the assignmentList where comment not equals to UPDATED_COMMENT
        defaultAssignmentShouldBeFound("comment.notEquals=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void getAllAssignmentsByCommentIsInShouldWork() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where comment in DEFAULT_COMMENT or UPDATED_COMMENT
        defaultAssignmentShouldBeFound("comment.in=" + DEFAULT_COMMENT + "," + UPDATED_COMMENT);

        // Get all the assignmentList where comment equals to UPDATED_COMMENT
        defaultAssignmentShouldNotBeFound("comment.in=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void getAllAssignmentsByCommentIsNullOrNotNull() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where comment is not null
        defaultAssignmentShouldBeFound("comment.specified=true");

        // Get all the assignmentList where comment is null
        defaultAssignmentShouldNotBeFound("comment.specified=false");
    }
                @Test
    @Transactional
    public void getAllAssignmentsByCommentContainsSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where comment contains DEFAULT_COMMENT
        defaultAssignmentShouldBeFound("comment.contains=" + DEFAULT_COMMENT);

        // Get all the assignmentList where comment contains UPDATED_COMMENT
        defaultAssignmentShouldNotBeFound("comment.contains=" + UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void getAllAssignmentsByCommentNotContainsSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);

        // Get all the assignmentList where comment does not contain DEFAULT_COMMENT
        defaultAssignmentShouldNotBeFound("comment.doesNotContain=" + DEFAULT_COMMENT);

        // Get all the assignmentList where comment does not contain UPDATED_COMMENT
        defaultAssignmentShouldBeFound("comment.doesNotContain=" + UPDATED_COMMENT);
    }


    @Test
    @Transactional
    public void getAllAssignmentsByBoxItemIsEqualToSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);
        Item boxItem = ItemResourceIT.createEntity(em);
        em.persist(boxItem);
        em.flush();
        assignment.setBoxItem(boxItem);
        assignmentRepository.saveAndFlush(assignment);
        Long boxItemId = boxItem.getId();

        // Get all the assignmentList where boxItem equals to boxItemId
        defaultAssignmentShouldBeFound("boxItemId.equals=" + boxItemId);

        // Get all the assignmentList where boxItem equals to boxItemId + 1
        defaultAssignmentShouldNotBeFound("boxItemId.equals=" + (boxItemId + 1));
    }


    @Test
    @Transactional
    public void getAllAssignmentsByBoxIsEqualToSomething() throws Exception {
        // Initialize the database
        assignmentRepository.saveAndFlush(assignment);
        Item box = ItemResourceIT.createEntity(em);
        em.persist(box);
        em.flush();
        assignment.setBox(box);
        assignmentRepository.saveAndFlush(assignment);
        Long boxId = box.getId();

        // Get all the assignmentList where box equals to boxId
        defaultAssignmentShouldBeFound("boxId.equals=" + boxId);

        // Get all the assignmentList where box equals to boxId + 1
        defaultAssignmentShouldNotBeFound("boxId.equals=" + (boxId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssignmentShouldBeFound(String filter) throws Exception {
        restAssignmentMockMvc.perform(get("/api/assignments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));

        // Check, that the count call also returns 1
        restAssignmentMockMvc.perform(get("/api/assignments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAssignmentShouldNotBeFound(String filter) throws Exception {
        restAssignmentMockMvc.perform(get("/api/assignments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAssignmentMockMvc.perform(get("/api/assignments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingAssignment() throws Exception {
        // Get the assignment
        restAssignmentMockMvc.perform(get("/api/assignments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAssignment() throws Exception {
        // Initialize the database
        assignmentService.save(assignment);

        int databaseSizeBeforeUpdate = assignmentRepository.findAll().size();

        // Update the assignment
        Assignment updatedAssignment = assignmentRepository.findById(assignment.getId()).get();
        // Disconnect from session so that the updates on updatedAssignment are not directly saved in db
        em.detach(updatedAssignment);
        updatedAssignment
            .description(UPDATED_DESCRIPTION)
            .comment(UPDATED_COMMENT);

        restAssignmentMockMvc.perform(put("/api/assignments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedAssignment)))
            .andExpect(status().isOk());

        // Validate the Assignment in the database
        List<Assignment> assignmentList = assignmentRepository.findAll();
        assertThat(assignmentList).hasSize(databaseSizeBeforeUpdate);
        Assignment testAssignment = assignmentList.get(assignmentList.size() - 1);
        assertThat(testAssignment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAssignment.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void updateNonExistingAssignment() throws Exception {
        int databaseSizeBeforeUpdate = assignmentRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignmentMockMvc.perform(put("/api/assignments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(assignment)))
            .andExpect(status().isBadRequest());

        // Validate the Assignment in the database
        List<Assignment> assignmentList = assignmentRepository.findAll();
        assertThat(assignmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAssignment() throws Exception {
        // Initialize the database
        assignmentService.save(assignment);

        int databaseSizeBeforeDelete = assignmentRepository.findAll().size();

        // Delete the assignment
        restAssignmentMockMvc.perform(delete("/api/assignments/{id}", assignment.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Assignment> assignmentList = assignmentRepository.findAll();
        assertThat(assignmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
