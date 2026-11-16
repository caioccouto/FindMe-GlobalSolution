package br.com.globalsolution.findme.dto;

import br.com.globalsolution.findme.model.TipoUsuario;

import java.time.LocalDateTime;

public record UsuarioResponse(Long id, String nome, String email, String telefone, TipoUsuario tipoUsuario,
                              LocalDateTime dataCadastro) {
}
