package com.api.repository;

import com.api.model.Journal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JournalRepository extends MongoRepository<Journal, String> {
    List<Journal> findByTitleLike(@Param("title") String title);
}
