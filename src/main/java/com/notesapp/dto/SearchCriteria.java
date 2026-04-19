package com.notesapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for search and filter criteria
 * Provides clean separation of concerns for search parameters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    
    /**
     * Full-text search keyword (searches title and content)
     */
    private String keyword;
    
    /**
     * Filter by tags - notes must contain ALL specified tags
     */
    private List<String> tags;
    
    /**
     * Filter notes updated after or on this date
     */
    private LocalDateTime startDate;
    
    /**
     * Filter notes updated before or on this date
     */
    private LocalDateTime endDate;
    
    /**
     * Filter by favorite status (optional)
     */
    private Boolean isFavorite;
    
    /**
     * Sort field (default: createdAt, options: title, createdAt, updatedAt)
     */
    @Builder.Default
    private String sortBy = "updatedAt";
    
    /**
     * Sort direction (asc or desc)
     */
    @Builder.Default
    private String sortDirection = "desc";
    
    /**
     * Validate search criteria
     */
    public void validate() {
        if (keyword != null && keyword.trim().length() > 500) {
            throw new IllegalArgumentException("Search keyword must be less than 500 characters");
        }
        
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        
        if (sortBy != null && !sortBy.matches("^(title|createdAt|updatedAt|isFavorite)$")) {
            throw new IllegalArgumentException("Invalid sort field. Must be: title, createdAt, updatedAt, isFavorite");
        }
        
        if (sortDirection != null && !sortDirection.matches("^(asc|desc)$")) {
            throw new IllegalArgumentException("Sort direction must be 'asc' or 'desc'");
        }
    }
}
