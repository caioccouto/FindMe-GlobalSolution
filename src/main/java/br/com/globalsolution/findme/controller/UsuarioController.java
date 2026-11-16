package br.com.globalsolution.findme.controller;

import br.com.globalsolution.findme.mapper.UsuarioMapper;
import br.com.globalsolution.findme.service.UsuarioService;
import br.com.globalsolution.findme.dto.ChangeTipoUsuarioRequest;
import br.com.globalsolution.findme.dto.UsuarioResponse;
import br.com.globalsolution.findme.mapper.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Validated
@Tag(name = "Usuários", description = "Administração de usuários (somente MODERADOR)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Lista usuários (paginado)")
    public PageResponse<UsuarioResponse> findAll(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                 @RequestParam(required = false, defaultValue = "10") Integer sizePage) {
        return PageResponse.from(usuarioService.findAll(page, sizePage).map(UsuarioMapper::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca usuário por id")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuarioService.findById(id)));
    }

    @PutMapping("/{id}/tipo")
    @Operation(summary = "Altera o tipo (papel) do usuário")
    public ResponseEntity<UsuarioResponse> changeTipo(@PathVariable @Positive Long id,
                                                      @RequestBody @Valid ChangeTipoUsuarioRequest request) {
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuarioService.changeTipo(id, request.tipoUsuario())));
    }
}
