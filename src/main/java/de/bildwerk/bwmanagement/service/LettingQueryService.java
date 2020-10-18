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

import de.bildwerk.bwmanagement.domain.Letting;
import de.bildwerk.bwmanagement.domain.*; // for static metamodels
import de.bildwerk.bwmanagement.repository.LettingRepository;
import de.bildwerk.bwmanagement.service.dto.LettingCriteria;

/**
 * Service for executing complex queries for {@link Letting} entities in the database.
 * The main input is a {@link LettingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Letting} or a {@link Page} of {@link Letting} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LettingQueryService extends QueryService<Letting> {

    private final Logger log = LoggerFactory.getLogger(LettingQueryService.class);

    private final LettingRepository lettingRepository;

    public LettingQueryService(LettingRepository lettingRepository) {
        this.lettingRepository = lettingRepository;
    }

    /**
     * Return a {@link List} of {@link Letting} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Letting> findByCriteria(LettingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Letting> specification = createSpecification(criteria);
        return lettingRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Letting} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Letting> findByCriteria(LettingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Letting> specification = createSpecification(criteria);
        return lettingRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LettingCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Letting> specification = createSpecification(criteria);
        return lettingRepository.count(specification);
    }

    /**
     * Function to convert {@link LettingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Letting> createSpecification(LettingCriteria criteria) {
        Specification<Letting> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Letting_.id));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), Letting_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), Letting_.endDate));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Letting_.name));
            }
            if (criteria.getLocation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLocation(), Letting_.location));
            }
            if (criteria.getItemId() != null) {
                specification = specification.and(buildSpecification(criteria.getItemId(),
                    root -> root.join(Letting_.item, JoinType.LEFT).get(Item_.id)));
            }
        }
        return specification;
    }
}
