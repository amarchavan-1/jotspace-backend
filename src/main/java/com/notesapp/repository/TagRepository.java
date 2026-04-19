package com.notesapp.repository;

import com.notesapp.model.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {
    List<Tag> findByUserId(String userId);
    Optional<Tag> findByIdAndUserId(String id, String userId);
    boolean existsByNameAndUserId(String name, String userId);
}
