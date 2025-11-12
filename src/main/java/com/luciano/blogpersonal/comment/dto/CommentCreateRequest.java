package com.luciano.blogpersonal.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CommentCreateRequest {
@NotBlank(message = "El contenido del comentario es obligatorio")
private String content;

@NotNull(message = "El ID del post es obligatorio")
private Long postId;

private Long parentId;


}
