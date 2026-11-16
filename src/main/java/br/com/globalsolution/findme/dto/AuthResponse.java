package br.com.globalsolution.findme.dto;

import br.com.globalsolution.findme.model.TipoUsuario;

public record AuthResponse(String token, String tokenType, long expiresInSeconds, String email,
                           TipoUsuario tipoUsuario) {
}
