package com.example.webcrawler.url;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<UrlEntity, String> {
    Optional<UrlEntity> findByUrl(String url);

    boolean existsByHash(String hash);

}