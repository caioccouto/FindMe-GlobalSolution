package br.com.globalsolution.findme.dto;

import br.com.globalsolution.findme.model.StatusCaso;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
        @NotNull(message = "status é obrigatório")
        StatusCaso status
) {
}
