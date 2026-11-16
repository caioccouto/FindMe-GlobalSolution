package br.com.globalsolution.findme.service;

import br.com.globalsolution.findme.model.TipoUsuario;
import br.com.globalsolution.findme.model.Usuario;
import org.springframework.data.domain.Page;

public interface UsuarioService {

    Usuario register(String nome, String email, String rawPassword, String telefone, TipoUsuario tipoUsuario);

    Page<Usuario> findAll(Integer page, Integer size);

    Usuario findById(Long id);

    Usuario changeTipo(Long id, TipoUsuario tipoUsuario);
}
