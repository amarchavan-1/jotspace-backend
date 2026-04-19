package com.notesapp.repository;

import com.notesapp.dto.SearchCriteria;
import com.notesapp.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface NoteRepositoryCustom {
    
    /**
     * Legacy method - kept for backward compatibility
     * Use searchNotesByCriteria instead for new features
     */
    Page<Note> searchNotesDynamic(String userId, String keyword, List<String> tags, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Enhanced search supporting all filter criteria
     * Uses MongoDB text indexing for efficient full-text search
     */
    Page<Note> searchNotesByCriteria(String userId, SearchCriteria criteria, Pageable pageable);
}
