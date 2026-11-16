package br.com.globalsolution.findme.controller;

import br.com.globalsolution.findme.mapper.UsuarioMapper;
import br.com.globalsolution.findme.model.Usuario;
import br.com.globalsolution.findme.security.JwtService;
import br.com.globalsolution.findme.security.SecurityUtils;
import br.com.globalsolution.findme.service.UsuarioService;
import br.com.globalsolution.findme.dto.AuthResponse;
import br.com.globalsolution.findme.dto.LoginRequest;
import br.com.globalsolution.findme.dto.RegisterRequest;
import br.com.globalsolution.findme.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Cadastro, login (JWT) e dados do usuário autenticado")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService, AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @SecurityRequirements
    @PostMapping("/register")
    @Operation(summary = "Cadastra um usuário",
            description = "tipoUsuario aceito: RESPONSAVEL ou COLABORADOR. MODERADOR só é atribuído por outro moderador.")
    public ResponseEntity<UsuarioResponse> register(@RequestBody @Valid RegisterRequest request) {
        Usuario created = usuarioService.register(request.nome(), request.email(), request.senha(),
                request.telefone(), request.tipoUsuario());
        return ResponseEntity.created(URI.create("/api/usuarios/" + created.getId()))
                .body(UsuarioMapper.toResponse(created));
    }

    @SecurityRequirements
    @PostMapping("/login")
    @Operation(summary = "Autentica e devolve o token JWT")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = jwtService.generateToken(usuario);

        return ResponseEntity.ok(
                new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds(),
                        usuario.getEmail(), usuario.getTipoUsuario()));
    }

    @GetMapping("/me")
    @Operation(summary = "Devolve os dados do dono do token")
    public ResponseEntity<UsuarioResponse> me() {
        return ResponseEntity.ok(UsuarioMapper.toResponse(SecurityUtils.currentUsuario()));
    }
}
