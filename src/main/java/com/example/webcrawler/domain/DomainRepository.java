package com.example.webcrawler.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DomainRepository extends MongoRepository<DomainEntity, String> {
    DomainEntity findByIdIgnoreCase(String id);

}
