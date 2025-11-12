package com.luciano.blogpersonal.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserUpdateRequest {
    @Size(min = 3, max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String name;

    @Size (min = 3, max = 50, message = "El apellido no puede tener más de 50 caracteres")
    private String lastName;

    @Email(message = "El correo electrónico no es válido")
    private String email;

    @Size(max = 500, message = "La biografía no puede tener más de 500 caracteres")
    private String bio;

    private String profileImage;
}
