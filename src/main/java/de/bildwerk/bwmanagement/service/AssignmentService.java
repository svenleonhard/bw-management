package de.bildwerk.bwmanagement.service;

import de.bildwerk.bwmanagement.domain.Assignment;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Assignment}.
 */
public interface AssignmentService {

    /**
     * Save a assignment.
     *
     * @param assignment the entity to save.
     * @return the persisted entity.
     */
    Assignment save(Assignment assignment);

    /**
     * Get all the assignments.
     *
     * @return the list of entities.
     */
    List<Assignment> findAll();


    /**
     * Get the "id" assignment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Assignment> findOne(Long id);

    /**
     * Delete the "id" assignment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
