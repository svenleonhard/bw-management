package de.bildwerk.bwmanagement.service;

import de.bildwerk.bwmanagement.domain.Content;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Content}.
 */
public interface ContentService {

    /**
     * Save a content.
     *
     * @param content the entity to save.
     * @return the persisted entity.
     */
    Content save(Content content);

    /**
     * Get all the contents.
     *
     * @return the list of entities.
     */
    List<Content> findAll();


    /**
     * Get the "id" content.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Content> findOne(Long id);

    /**
     * Delete the "id" content.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
