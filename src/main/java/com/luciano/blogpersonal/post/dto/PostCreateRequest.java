package com.luciano.blogpersonal.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "El titulo es obligatorio")
    @Size(min = 3, max = 100, message = "El titulo debe tener entre 3 y 100 caracteres")
    private String title;

    @NotBlank(message = "El extracto es obligatorio")
    @Size(min = 3, max = 200, message = "El extracto debe tener entre 3 y 200 caracteres")
    private String excerpt;

    @NotBlank(message = "El contenido es obligatorio")
    private String content;

    private String featuredImage;

    private boolean published = false;

    private Set<Long> categoryIds = new HashSet<>();

    private Set<Long> tagIds = new HashSet<>();
}
