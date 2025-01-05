package com.example.webcrawler.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DomainService {
    private final DomainRepository domainRepository;

    @Autowired
    public DomainService(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public void saveOrUpdateDomain(String domainId, String robots) {
        DomainEntity domain = domainRepository.findByIdIgnoreCase(domainId);
        if (domain == null) {
            domain = new DomainEntity(robots, LocalDateTime.now(), domainId);
        } else {
            domain.setRobots(robots);
            domain.setLastCrawlTime(LocalDateTime.now());
        }
        domainRepository.save(domain);
    }

    public Optional<DomainEntity> getDomain(String domainId) {
        return Optional.ofNullable(domainRepository.findByIdIgnoreCase(domainId));
    }

}
