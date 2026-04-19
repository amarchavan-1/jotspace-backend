package com.notesapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper for paginated search results
 * Provides consistent API response format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse<T> {
    
    @JsonProperty("content")
    private List<T> content;
    
    @JsonProperty("page")
    private int pageNumber;
    
    @JsonProperty("size")
    private int pageSize;
    
    @JsonProperty("totalElements")
    private long totalElements;
    
    @JsonProperty("totalPages")
    private int totalPages;
    
    @JsonProperty("isLast")
    private boolean isLast;
    
    @JsonProperty("isEmpty")
    private boolean isEmpty;
    
    /**
     * Factory method to create from Spring Page object
     */
    public static <T> SearchResponse<T> from(Page<T> page) {
        return SearchResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLast(page.isLast())
                .isEmpty(page.isEmpty())
                .build();
    }
}
