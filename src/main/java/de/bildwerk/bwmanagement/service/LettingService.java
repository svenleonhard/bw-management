package de.bildwerk.bwmanagement.service;

import de.bildwerk.bwmanagement.domain.Letting;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Letting}.
 */
public interface LettingService {

    /**
     * Save a letting.
     *
     * @param letting the entity to save.
     * @return the persisted entity.
     */
    Letting save(Letting letting);

    /**
     * Get all the lettings.
     *
     * @return the list of entities.
     */
    List<Letting> findAll();


    /**
     * Get the "id" letting.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Letting> findOne(Long id);

    /**
     * Delete the "id" letting.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
