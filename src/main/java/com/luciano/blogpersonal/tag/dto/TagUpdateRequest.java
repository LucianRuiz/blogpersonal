package com.luciano.blogpersonal.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TagUpdateRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 30, min = 2, message = "El nombre debe tener entre 2 y 30 caracteres")
    private String name;
}
