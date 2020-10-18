package de.bildwerk.bwmanagement.service;

import de.bildwerk.bwmanagement.domain.Image;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Image}.
 */
public interface ImageService {

    /**
     * Save a image.
     *
     * @param image the entity to save.
     * @return the persisted entity.
     */
    Image save(Image image);

    /**
     * Get all the images.
     *
     * @return the list of entities.
     */
    List<Image> findAll();


    /**
     * Get the "id" image.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Image> findOne(Long id);

    /**
     * Delete the "id" image.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
