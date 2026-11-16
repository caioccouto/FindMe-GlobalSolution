package br.com.globalsolution.findme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Dados para registrar ou atualizar um caso de desaparecimento")
public record CasoRequest(
        @Schema(example = "Maria Silva")
        @NotBlank(message = "nomeDesaparecido é obrigatório")
        @Size(max = 150, message = "nomeDesaparecido deve ter no máximo 150 caracteres")
        String nomeDesaparecido,

        @Schema(example = "12")
        @NotNull(message = "idade é obrigatória")
        @Min(value = 0, message = "idade deve estar entre 0 e 17")
        @Max(value = 17, message = "idade deve estar entre 0 e 17")
        Integer idade,

        @NotBlank(message = "descricao é obrigatória")
        @Size(max = 2000, message = "descricao deve ter no máximo 2000 caracteres")
        String descricao,

        @NotBlank(message = "ultimaLocalizacaoConhecida é obrigatória")
        @Size(max = 300, message = "ultimaLocalizacaoConhecida deve ter no máximo 300 caracteres")
        String ultimaLocalizacaoConhecida,

        @NotNull(message = "dataDesaparecimento é obrigatória")
        @PastOrPresent(message = "dataDesaparecimento não pode estar no futuro")
        LocalDate dataDesaparecimento
) {
}
