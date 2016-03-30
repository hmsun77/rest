package com.api.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongodbConfig extends AbstractMongoConfiguration {

    @Override
    public String getDatabaseName() {
        return "db";
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient("192.168.99.100:32769");
    }

}
