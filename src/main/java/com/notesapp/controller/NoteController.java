package com.notesapp.controller;

import com.notesapp.dto.NoteRequest;
import com.notesapp.dto.NoteResponse;
import com.notesapp.dto.SearchCriteria;
import com.notesapp.security.UserPrincipal;
import com.notesapp.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Get all active notes with pagination and sorting
     */
    @GetMapping
    public ResponseEntity<Page<NoteResponse>> getAllNotes(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "updatedAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NoteResponse> notes = noteService.getAllNotes(currentUser, pageable);
        return ResponseEntity.ok(notes);
    }

    /**
     * Legacy search endpoint - kept for backward compatibility
     * Use /notes/search-advanced for new features
     */
    @GetMapping("/search")
    public ResponseEntity<Page<NoteResponse>> searchNotes(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "tags", required = false) java.util.List<String> tags,
            @RequestParam(name = "startDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "updatedAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NoteResponse> notes = noteService.searchNotes(currentUser, query, tags, startDate, endDate, pageable);
        return ResponseEntity.ok(notes);
    }

    /**
     * Advanced search with enhanced filtering capabilities
     * Supports keyword search, tags, date ranges, favorites, and custom sorting
     */
    @PostMapping("/search-advanced")
    public ResponseEntity<Page<NoteResponse>> searchAdvanced(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody(required = false) SearchCriteria criteria,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        // Initialize SearchCriteria if not provided (empty search)
        if (criteria == null) {
            criteria = SearchCriteria.builder().build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NoteResponse> notes = noteService.searchNotesByCriteria(currentUser, criteria, pageable);
        return ResponseEntity.ok(notes);
    }

    /**
     * Get archived/trash notes with pagination
     */
    @GetMapping("/trash")
    public ResponseEntity<Page<NoteResponse>> getTrashNotes(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "updatedAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NoteResponse> notes = noteService.getTrashNotes(currentUser, pageable);
        return ResponseEntity.ok(notes);
    }

    /**
     * Create a new note
     */
    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @Valid @RequestBody NoteRequest noteRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        NoteResponse note = noteService.createNote(noteRequest, currentUser);
        return new ResponseEntity<>(note, HttpStatus.CREATED);
    }

    /**
     * Update an existing note
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable("id") String noteId,
            @Valid @RequestBody NoteRequest noteRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        NoteResponse note = noteService.updateNote(noteId, noteRequest, currentUser);
        return ResponseEntity.ok(note);
    }

    /**
     * Soft delete - move note to trash
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNote(
            @PathVariable("id") String noteId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        noteService.deleteNote(noteId, currentUser);
        return ResponseEntity.ok("Note moved to trash successfully.");
    }

    /**
     * Permanently delete - hard delete
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<String> permanentlyDeleteNote(
            @PathVariable("id") String noteId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        noteService.permanentlyDeleteNote(noteId, currentUser);
        return ResponseEntity.ok("Note permanently deleted successfully.");
    }
}
