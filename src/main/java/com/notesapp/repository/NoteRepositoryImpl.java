package com.notesapp.repository;

import com.notesapp.dto.SearchCriteria;
import com.notesapp.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class NoteRepositoryImpl implements NoteRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public NoteRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Note> searchNotesDynamic(String userId, String keyword, List<String> tags, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("isDeleted").is(false));

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Use regex for title (prefix match is faster)
            Criteria keywordCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex("^" + escapeRegex(keyword), "i"),
                    Criteria.where("content").regex(escapeRegex(keyword), "i")
            );
            query.addCriteria(keywordCriteria);
        }

        if (tags != null && !tags.isEmpty()) {
            query.addCriteria(Criteria.where("tagIds").in(tags)); // Changed from .all() to .in() for better flexibility
        }

        if (startDate != null || endDate != null) {
            Criteria dateCriteria = Criteria.where("updatedAt");
            if (startDate != null) {
                dateCriteria.gte(startDate);
            }
            if (endDate != null) {
                dateCriteria.lte(endDate);
            }
            query.addCriteria(dateCriteria);
        }

        long count = mongoTemplate.count(query, Note.class);

        query.with(pageable);
        List<Note> notes = mongoTemplate.find(query, Note.class);

        return new PageImpl<>(notes, pageable, count);
    }

    @Override
    public Page<Note> searchNotesByCriteria(String userId, SearchCriteria criteria, Pageable pageable) {
        // Validate criteria
        criteria.validate();
        
        Query query = buildSearchQuery(userId, criteria);
        
        // Count total results
        long count = mongoTemplate.count(query, Note.class);
        
        // Apply pagination and sorting
        applySorting(query, criteria);
        query.with(pageable);
        
        List<Note> notes = mongoTemplate.find(query, Note.class);

        return new PageImpl<>(notes, pageable, count);
    }

    /**
     * Builds the MongoDB query from SearchCriteria
     */
    private Query buildSearchQuery(String userId, SearchCriteria criteria) {
        Query query = new Query();
        
        // Always filter by userId and non-deleted notes
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("isDeleted").is(false));

        // Full-text keyword search (title and content)
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            String keyword = criteria.getKeyword().trim();
            Criteria keywordCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(escapeRegex(keyword), "i"),
                    Criteria.where("content").regex(escapeRegex(keyword), "i")
            );
            query.addCriteria(keywordCriteria);
        }

        // Tag filtering (IN operator - note contains any of the tags)
        if (criteria.getTags() != null && !criteria.getTags().isEmpty()) {
            query.addCriteria(Criteria.where("tagIds").in(criteria.getTags()));
        }

        // Date range filtering
        if (criteria.getStartDate() != null || criteria.getEndDate() != null) {
            Criteria dateCriteria = Criteria.where("updatedAt");
            if (criteria.getStartDate() != null) {
                dateCriteria.gte(criteria.getStartDate());
            }
            if (criteria.getEndDate() != null) {
                dateCriteria.lte(criteria.getEndDate());
            }
            query.addCriteria(dateCriteria);
        }

        // Filter by favorite status if specified
        if (criteria.getIsFavorite() != null) {
            query.addCriteria(Criteria.where("isFavorite").is(criteria.getIsFavorite()));
        }

        return query;
    }

    /**
     * Applies sorting to the query
     */
    private void applySorting(Query query, SearchCriteria criteria) {
        Sort.Direction direction = "asc".equalsIgnoreCase(criteria.getSortDirection()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        
        query.with(Sort.by(direction, criteria.getSortBy()));
    }

    /**
     * Escapes special regex characters for safe MongoDB queries
     */
    private String escapeRegex(String str) {
        return str.replaceAll("([.^$*+?{}\\[\\]()\\\\|])", "\\\\$1");
    }
}
