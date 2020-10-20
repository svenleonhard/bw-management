package de.bildwerk.bwmanagement.web.rest;

import de.bildwerk.bwmanagement.domain.Letting;
import de.bildwerk.bwmanagement.service.LettingService;
import de.bildwerk.bwmanagement.web.rest.errors.BadRequestAlertException;
import de.bildwerk.bwmanagement.service.dto.LettingCriteria;
import de.bildwerk.bwmanagement.service.LettingQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link de.bildwerk.bwmanagement.domain.Letting}.
 */
@RestController
@RequestMapping("/api")
public class LettingResource {

    private final Logger log = LoggerFactory.getLogger(LettingResource.class);

    private static final String ENTITY_NAME = "letting";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LettingService lettingService;

    private final LettingQueryService lettingQueryService;

    public LettingResource(LettingService lettingService, LettingQueryService lettingQueryService) {
        this.lettingService = lettingService;
        this.lettingQueryService = lettingQueryService;
    }

    /**
     * {@code POST  /lettings} : Create a new letting.
     *
     * @param letting the letting to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new letting, or with status {@code 400 (Bad Request)} if the letting has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lettings")
    public ResponseEntity<Letting> createLetting(@Valid @RequestBody Letting letting) throws URISyntaxException {
        log.debug("REST request to save Letting : {}", letting);
        if (letting.getId() != null) {
            throw new BadRequestAlertException("A new letting cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Letting result = lettingService.save(letting);
        return ResponseEntity.created(new URI("/api/lettings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lettings} : Updates an existing letting.
     *
     * @param letting the letting to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated letting,
     * or with status {@code 400 (Bad Request)} if the letting is not valid,
     * or with status {@code 500 (Internal Server Error)} if the letting couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lettings")
    public ResponseEntity<Letting> updateLetting(@Valid @RequestBody Letting letting) throws URISyntaxException {
        log.debug("REST request to update Letting : {}", letting);
        if (letting.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Letting result = lettingService.save(letting);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, letting.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /lettings} : get all the lettings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lettings in body.
     */
    @GetMapping("/lettings")
    public ResponseEntity<List<Letting>> getAllLettings(LettingCriteria criteria) {
        log.debug("REST request to get Lettings by criteria: {}", criteria);
        List<Letting> entityList = lettingQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /lettings/count} : count all the lettings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/lettings/count")
    public ResponseEntity<Long> countLettings(LettingCriteria criteria) {
        log.debug("REST request to count Lettings by criteria: {}", criteria);
        return ResponseEntity.ok().body(lettingQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /lettings/:id} : get the "id" letting.
     *
     * @param id the id of the letting to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the letting, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lettings/{id}")
    public ResponseEntity<Letting> getLetting(@PathVariable Long id) {
        log.debug("REST request to get Letting : {}", id);
        Optional<Letting> letting = lettingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(letting);
    }

    /**
     * {@code DELETE  /lettings/:id} : delete the "id" letting.
     *
     * @param id the id of the letting to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lettings/{id}")
    public ResponseEntity<Void> deleteLetting(@PathVariable Long id) {
        log.debug("REST request to delete Letting : {}", id);
        lettingService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
