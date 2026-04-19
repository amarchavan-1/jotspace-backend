package com.notesapp.repository;

import com.notesapp.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<Note, String>, NoteRepositoryCustom {
    Page<Note> findByUserIdAndIsDeletedFalse(String userId, Pageable pageable);
    
    Page<Note> findByUserIdAndIsDeletedTrue(String userId, Pageable pageable);
}
