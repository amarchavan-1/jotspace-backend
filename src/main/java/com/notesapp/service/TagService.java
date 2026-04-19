package com.notesapp.service;

import com.notesapp.dto.TagRequest;
import com.notesapp.dto.TagResponse;
import com.notesapp.exception.ResourceNotFoundException;
import com.notesapp.model.Tag;
import com.notesapp.repository.TagRepository;
import com.notesapp.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagResponse> getAllTags(UserPrincipal currentUser) {
        return tagRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TagResponse createTag(TagRequest request, UserPrincipal currentUser) {
        if (tagRepository.existsByNameAndUserId(request.getName(), currentUser.getId())) {
            throw new IllegalArgumentException("Tag with this name already exists");
        }

        Tag tag = Tag.builder()
                .name(request.getName())
                .color(request.getColor())
                .userId(currentUser.getId())
                .build();

        return mapToResponse(tagRepository.save(tag));
    }

    public TagResponse updateTag(String tagId, TagRequest request, UserPrincipal currentUser) {
        Tag tag = tagRepository.findByIdAndUserId(tagId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));

        tag.setName(request.getName());
        tag.setColor(request.getColor());

        return mapToResponse(tagRepository.save(tag));
    }

    public void deleteTag(String tagId, UserPrincipal currentUser) {
        Tag tag = tagRepository.findByIdAndUserId(tagId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        
        tagRepository.delete(tag);
        // Note: cascade delete in Note's tagIds could be implemented, but simple orphaned IDs are fine.
    }

    public TagResponse mapToResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}
