package br.com.globalsolution.findme.mapper;

import br.com.globalsolution.findme.model.Usuario;
import br.com.globalsolution.findme.dto.UsuarioResponse;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getTipoUsuario(),
                usuario.getDataCadastro()
        );
    }
}
