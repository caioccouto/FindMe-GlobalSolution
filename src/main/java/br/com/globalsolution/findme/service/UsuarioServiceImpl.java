package br.com.globalsolution.findme.service;

import br.com.globalsolution.findme.exceptions.EmailAlreadyRegisteredException;
import br.com.globalsolution.findme.exceptions.ResourceNotFoundException;
import br.com.globalsolution.findme.model.TipoUsuario;
import br.com.globalsolution.findme.model.Usuario;
import br.com.globalsolution.findme.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario register(String nome, String email, String rawPassword, String telefone, TipoUsuario tipoUsuario) {
        if (tipoUsuario == TipoUsuario.MODERADOR) {
            throw new IllegalArgumentException("O cadastro público permite apenas RESPONSAVEL ou COLABORADOR");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(rawPassword));
        usuario.setTelefone(telefone);
        usuario.setTipoUsuario(tipoUsuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Page<Usuario> findAll(Integer page, Integer size) {
        return usuarioRepository.findAll(Pageable.ofSize(size).withPage(page));
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    @Override
    public Usuario changeTipo(Long id, TipoUsuario tipoUsuario) {
        Usuario usuario = findById(id);
        usuario.setTipoUsuario(tipoUsuario);
        return usuarioRepository.save(usuario);
    }
}
