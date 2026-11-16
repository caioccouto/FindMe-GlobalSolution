package br.com.globalsolution.findme.dto;

import br.com.globalsolution.findme.model.TipoUsuario;
import jakarta.validation.constraints.NotNull;

public record ChangeTipoUsuarioRequest(
        @NotNull(message = "tipoUsuario é obrigatório")
        TipoUsuario tipoUsuario
) {
}
