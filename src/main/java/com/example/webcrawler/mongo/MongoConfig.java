package com.example.webcrawler.mongo;

import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Override
    public com.mongodb.client.MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public boolean autoIndexCreation() {
        return true;
    }

}