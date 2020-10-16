package de.bildwerk.bwmanagement.repository;

import de.bildwerk.bwmanagement.domain.Letting;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Letting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LettingRepository extends JpaRepository<Letting, Long> {
}
