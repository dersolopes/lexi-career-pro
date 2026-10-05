package com.lexicareer.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    @NotBlank(message = "E-mail não pode ficar vazio")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha não pode ficar vazia")
    private String password;
}
