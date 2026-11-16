package br.com.globalsolution.findme.dto;

import br.com.globalsolution.findme.model.TipoUsuario;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "nome é obrigatório")
        @Size(max = 100, message = "nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        String email,

        @NotBlank(message = "senha é obrigatória")
        @Size(min = 8, max = 72, message = "senha deve ter entre 8 e 72 caracteres")
        String senha,

        @Pattern(regexp = "^\\+?[0-9()\\-\\s]{8,20}$", message = "telefone inválido")
        String telefone,

        @NotNull(message = "tipoUsuario é obrigatório (RESPONSAVEL ou COLABORADOR)")
        TipoUsuario tipoUsuario
) {
}
