package com.notesapp.service;

import com.notesapp.dto.NoteRequest;
import com.notesapp.dto.NoteResponse;
import com.notesapp.dto.SearchCriteria;
import com.notesapp.exception.ResourceNotFoundException;
import com.notesapp.exception.UnauthorizedAccessException;
import com.notesapp.model.Note;
import com.notesapp.repository.NoteRepository;
import com.notesapp.repository.TagRepository;
import com.notesapp.dto.TagResponse;
import com.notesapp.model.Tag;
import com.notesapp.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    public NoteService(NoteRepository noteRepository, TagRepository tagRepository) {
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Get all active notes for the current user with pagination
     */
    public Page<NoteResponse> getAllNotes(UserPrincipal currentUser, Pageable pageable) {
        Page<Note> notes = noteRepository.findByUserIdAndIsDeletedFalse(currentUser.getId(), pageable);
        return notes.map(this::mapToResponse);
    }

    /**
     * Legacy search method - kept for backward compatibility
     */
    public Page<NoteResponse> searchNotes(UserPrincipal currentUser, String query, java.util.List<String> tags, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, Pageable pageable) {
        Page<Note> notes = noteRepository.searchNotesDynamic(currentUser.getId(), query, tags, startDate, endDate, pageable);
        return notes.map(this::mapToResponse);
    }

    /**
     * Enhanced search using SearchCriteria DTO
     * Supports keyword search, tag filtering, date range filtering, and sorting
     */
    public Page<NoteResponse> searchNotesByCriteria(UserPrincipal currentUser, SearchCriteria criteria, Pageable pageable) {
        Page<Note> notes = noteRepository.searchNotesByCriteria(currentUser.getId(), criteria, pageable);
        return notes.map(this::mapToResponse);
    }

    /**
     * Get trash (soft-deleted) notes for the current user
     */
    public Page<NoteResponse> getTrashNotes(UserPrincipal currentUser, Pageable pageable) {
        Page<Note> notes = noteRepository.findByUserIdAndIsDeletedTrue(currentUser.getId(), pageable);
        return notes.map(this::mapToResponse);
    }

    /**
     * Create a new note
     */
    public NoteResponse createNote(NoteRequest noteRequest, UserPrincipal currentUser) {
        Note note = Note.builder()
                .title(noteRequest.getTitle())
                .content(noteRequest.getContent())
                .userId(currentUser.getId())
                .tagIds(noteRequest.getTagIds() != null ? noteRequest.getTagIds() : new ArrayList<>())
                .isFavorite(noteRequest.getIsFavorite() != null ? noteRequest.getIsFavorite() : false)
                .build();

        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }

    /**
     * Update an existing note
     */
    public NoteResponse updateNote(String noteId, NoteRequest noteRequest, UserPrincipal currentUser) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));

        if (!note.getUserId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this note.");
        }

        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());
        if (noteRequest.getTagIds() != null) {
            note.setTagIds(noteRequest.getTagIds());
        }
        if (noteRequest.getIsFavorite() != null) {
            note.setFavorite(noteRequest.getIsFavorite());
        }

        Note updatedNote = noteRepository.save(note);
        return mapToResponse(updatedNote);
    }

    /**
     * Soft delete - moves note to trash
     */
    public void deleteNote(String noteId, UserPrincipal currentUser) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));

        if (!note.getUserId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this note.");
        }

        note.setDeleted(true);
        noteRepository.save(note);
    }

    /**
     * Permanently delete a note (hard delete)
     */
    public void permanentlyDeleteNote(String noteId, UserPrincipal currentUser) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));

        if (!note.getUserId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to permanently delete this note.");
        }

        noteRepository.delete(note);
    }

    /**
     * Map Note entity to NoteResponse DTO
     */
    private NoteResponse mapToResponse(Note note) {
        java.util.List<TagResponse> tagResponses = new ArrayList<>();
        if (note.getTagIds() != null && !note.getTagIds().isEmpty()) {
            Iterable<Tag> tags = tagRepository.findAllById(note.getTagIds());
            for (Tag tag : tags) {
                tagResponses.add(TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .color(tag.getColor())
                        .createdAt(tag.getCreatedAt())
                        .updatedAt(tag.getUpdatedAt())
                        .build());
            }
        }

        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .tags(tagResponses)
                .isFavorite(note.isFavorite())
                .isDeleted(note.isDeleted())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
