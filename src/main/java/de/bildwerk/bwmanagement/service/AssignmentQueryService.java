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

import de.bildwerk.bwmanagement.domain.Assignment;
import de.bildwerk.bwmanagement.domain.*; // for static metamodels
import de.bildwerk.bwmanagement.repository.AssignmentRepository;
import de.bildwerk.bwmanagement.service.dto.AssignmentCriteria;

/**
 * Service for executing complex queries for {@link Assignment} entities in the database.
 * The main input is a {@link AssignmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Assignment} or a {@link Page} of {@link Assignment} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AssignmentQueryService extends QueryService<Assignment> {

    private final Logger log = LoggerFactory.getLogger(AssignmentQueryService.class);

    private final AssignmentRepository assignmentRepository;

    public AssignmentQueryService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Return a {@link List} of {@link Assignment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Assignment> findByCriteria(AssignmentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Assignment> specification = createSpecification(criteria);
        return assignmentRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Assignment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Assignment> findByCriteria(AssignmentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Assignment> specification = createSpecification(criteria);
        return assignmentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AssignmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Assignment> specification = createSpecification(criteria);
        return assignmentRepository.count(specification);
    }

    /**
     * Function to convert {@link AssignmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Assignment> createSpecification(AssignmentCriteria criteria) {
        Specification<Assignment> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Assignment_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Assignment_.description));
            }
            if (criteria.getComment() != null) {
                specification = specification.and(buildStringSpecification(criteria.getComment(), Assignment_.comment));
            }
            if (criteria.getBoxItemId() != null) {
                specification = specification.and(buildSpecification(criteria.getBoxItemId(),
                    root -> root.join(Assignment_.boxItem, JoinType.LEFT).get(Item_.id)));
            }
            if (criteria.getBoxId() != null) {
                specification = specification.and(buildSpecification(criteria.getBoxId(),
                    root -> root.join(Assignment_.box, JoinType.LEFT).get(Item_.id)));
            }
        }
        return specification;
    }
}
