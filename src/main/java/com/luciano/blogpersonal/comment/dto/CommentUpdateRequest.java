package com.luciano.blogpersonal.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CommentUpdateRequest {

    @NotBlank(message = "El contenido del comentario es obligatorio")
    private String content;
}
