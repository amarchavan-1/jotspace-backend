package com.notesapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagRequest {
    @NotBlank(message = "Tag name is required")
    private String name;

    @NotBlank(message = "Tag color is required")
    private String color;
}
