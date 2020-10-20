package de.bildwerk.bwmanagement.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import de.bildwerk.bwmanagement.domain.Content;
import de.bildwerk.bwmanagement.domain.*; // for static metamodels
import de.bildwerk.bwmanagement.repository.ContentRepository;
import de.bildwerk.bwmanagement.service.dto.ContentCriteria;

/**
 * Service for executing complex queries for {@link Content} entities in the database.
 * The main input is a {@link ContentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Content} or a {@link Page} of {@link Content} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ContentQueryService extends QueryService<Content> {

    private final Logger log = LoggerFactory.getLogger(ContentQueryService.class);

    private final ContentRepository contentRepository;

    public ContentQueryService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    /**
     * Return a {@link List} of {@link Content} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Content> findByCriteria(ContentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Content> specification = createSpecification(criteria);
        return contentRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Content} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Content> findByCriteria(ContentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Content> specification = createSpecification(criteria);
        return contentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ContentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Content> specification = createSpecification(criteria);
        return contentRepository.count(specification);
    }

    /**
     * Function to convert {@link ContentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Content> createSpecification(ContentCriteria criteria) {
        Specification<Content> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Content_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Content_.description));
            }
            if (criteria.getItemId() != null) {
                specification = specification.and(buildSpecification(criteria.getItemId(),
                    root -> root.join(Content_.item, JoinType.LEFT).get(Item_.id)));
            }
        }
        return specification;
    }
}
