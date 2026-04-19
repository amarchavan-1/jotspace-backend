package com.notesapp.controller;

import com.notesapp.dto.TagRequest;
import com.notesapp.dto.TagResponse;
import com.notesapp.security.UserPrincipal;
import com.notesapp.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(tagService.getAllTags(currentUser));
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest tagRequest,
                                                 @AuthenticationPrincipal UserPrincipal currentUser) {
        return new ResponseEntity<>(tagService.createTag(tagRequest, currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(@PathVariable("id") String id,
                                                 @Valid @RequestBody TagRequest tagRequest,
                                                 @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(tagService.updateTag(id, tagRequest, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable("id") String id,
                                            @AuthenticationPrincipal UserPrincipal currentUser) {
        tagService.deleteTag(id, currentUser);
        return ResponseEntity.ok("Tag deleted successfully");
    }
}
