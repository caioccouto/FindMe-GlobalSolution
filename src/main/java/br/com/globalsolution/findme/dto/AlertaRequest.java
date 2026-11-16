package br.com.globalsolution.findme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AlertaRequest(
        @NotNull(message = "casoId é obrigatório")
        @Positive(message = "casoId deve ser positivo")
        Long casoId,

        @NotBlank(message = "titulo é obrigatório")
        @Size(max = 150, message = "titulo deve ter no máximo 150 caracteres")
        String titulo,

        @NotBlank(message = "descricaoPublica é obrigatória")
        @Size(max = 1000, message = "descricaoPublica deve ter no máximo 1000 caracteres")
        String descricaoPublica
) {
}
