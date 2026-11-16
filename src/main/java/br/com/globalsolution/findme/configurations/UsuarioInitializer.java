package br.com.globalsolution.findme.configurations;

import br.com.globalsolution.findme.model.TipoUsuario;
import br.com.globalsolution.findme.model.Usuario;
import br.com.globalsolution.findme.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String moderadorEmail;
    private final String moderadorSenha;
    private final String responsavelEmail;
    private final String responsavelSenha;
    private final String colaboradorEmail;
    private final String colaboradorSenha;

    public UsuarioInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                              @Value("${app.seed.moderador.email}") String moderadorEmail,
                              @Value("${app.seed.moderador.senha}") String moderadorSenha,
                              @Value("${app.seed.responsavel.email}") String responsavelEmail,
                              @Value("${app.seed.responsavel.senha}") String responsavelSenha,
                              @Value("${app.seed.colaborador.email}") String colaboradorEmail,
                              @Value("${app.seed.colaborador.senha}") String colaboradorSenha) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.moderadorEmail = moderadorEmail;
        this.moderadorSenha = moderadorSenha;
        this.responsavelEmail = responsavelEmail;
        this.responsavelSenha = responsavelSenha;
        this.colaboradorEmail = colaboradorEmail;
        this.colaboradorSenha = colaboradorSenha;
    }

    @Override
    public void run(ApplicationArguments args) {
        criarSeNaoExistir("Moderador Find Me", moderadorEmail, moderadorSenha, "11900000001", TipoUsuario.MODERADOR);
        criarSeNaoExistir("Responsável Teste", responsavelEmail, responsavelSenha, "11900000002", TipoUsuario.RESPONSAVEL);
        criarSeNaoExistir("Colaborador Teste", colaboradorEmail, colaboradorSenha, "11900000003", TipoUsuario.COLABORADOR);
    }

    private void criarSeNaoExistir(String nome, String email, String senha, String telefone, TipoUsuario tipo) {
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setTelefone(telefone);
        usuario.setTipoUsuario(tipo);
        usuarioRepository.save(usuario);
    }
}
