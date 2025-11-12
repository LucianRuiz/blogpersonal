package com.luciano.blogpersonal.post.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PostUpdateRequest {
    @Size (min = 3, max = 100, message = "El titulo debe tener entre 3 y 100 caracteres")
    private String title;

    @Size (max = 200, message = "El extracto no debe superar los 200 caracteres")
    private String excerpt;

    private String content;

    private String featuredImage;

    private boolean published;

    private Set<Long> categoryIds = new HashSet<>();

    private Set<Long> tagIds = new HashSet<>();
}
