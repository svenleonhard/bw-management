package de.bildwerk.bwmanagement.service.impl;

import de.bildwerk.bwmanagement.service.LettingService;
import de.bildwerk.bwmanagement.domain.Letting;
import de.bildwerk.bwmanagement.repository.LettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Letting}.
 */
@Service
@Transactional
public class LettingServiceImpl implements LettingService {

    private final Logger log = LoggerFactory.getLogger(LettingServiceImpl.class);

    private final LettingRepository lettingRepository;

    public LettingServiceImpl(LettingRepository lettingRepository) {
        this.lettingRepository = lettingRepository;
    }

    @Override
    public Letting save(Letting letting) {
        log.debug("Request to save Letting : {}", letting);
        return lettingRepository.save(letting);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Letting> findAll() {
        log.debug("Request to get all Lettings");
        return lettingRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Letting> findOne(Long id) {
        log.debug("Request to get Letting : {}", id);
        return lettingRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Letting : {}", id);
        lettingRepository.deleteById(id);
    }
}
