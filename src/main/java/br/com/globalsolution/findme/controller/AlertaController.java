package br.com.globalsolution.findme.controller;

import br.com.globalsolution.findme.assembler.AlertaModelAssembler;
import br.com.globalsolution.findme.model.Alerta;
import br.com.globalsolution.findme.model.TipoUsuario;
import br.com.globalsolution.findme.security.SecurityUtils;
import br.com.globalsolution.findme.service.AlertaService;
import br.com.globalsolution.findme.dto.AlertaFiltro;
import br.com.globalsolution.findme.dto.AlertaRequest;
import br.com.globalsolution.findme.dto.AlertaResponse;
import br.com.globalsolution.findme.dto.AlertaUpdateRequest;
import br.com.globalsolution.findme.mapper.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/alertas")
@Validated
@Tag(name = "Alertas", description = "Versão pública e autorizada dos casos (dados minimizados)")
public class AlertaController {

    private final AlertaService alertaService;
    private final AlertaModelAssembler alertaModelAssembler;

    public AlertaController(AlertaService alertaService, AlertaModelAssembler alertaModelAssembler) {
        this.alertaService = alertaService;
        this.alertaModelAssembler = alertaModelAssembler;
    }

    @GetMapping
    @Operation(summary = "Lista alertas públicos ativos (cache)",
            description = "Qualquer usuário autenticado. sortBy: id, titulo, dataPublicacao, caso.nomeDesaparecido, caso.idade.")
    public PageResponse<EntityModel<AlertaResponse>> findPublicos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) @PositiveOrZero Integer idadeMin,
            @RequestParam(required = false) @PositiveOrZero Integer idadeMax,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer sizePage,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {
        AlertaFiltro filtro = new AlertaFiltro(nome, idadeMin, idadeMax, null);
        return PageResponse.from(alertaService.findPublicos(filtro, page, sizePage, sortBy, direction)
                .map(alertaModelAssembler::toModel));
    }

    @GetMapping("/recentes")
    @Operation(summary = "Alertas ativos publicados nos últimos N dias (JPQL com cache)")
    public PageResponse<EntityModel<AlertaResponse>> findRecentes(
            @RequestParam(required = false, defaultValue = "7") Integer dias,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer sizePage,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {
        return PageResponse.from(alertaService.findRecentes(dias, page, sizePage, sortBy, direction)
                .map(alertaModelAssembler::toModel));
    }

    @PreAuthorize("hasRole('MODERADOR')")
    @GetMapping("/admin")
    @Operation(summary = "Lista todos os alertas, ativos ou não (somente MODERADOR)")
    public PageResponse<EntityModel<AlertaResponse>> findAll(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) @PositiveOrZero Integer idadeMin,
            @RequestParam(required = false) @PositiveOrZero Integer idadeMax,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer sizePage,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {
        AlertaFiltro filtro = new AlertaFiltro(nome, idadeMin, idadeMax, ativo);
        return PageResponse.from(alertaService.findAll(filtro, page, sizePage, sortBy, direction)
                .map(alertaModelAssembler::toModel));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca alerta por id",
            description = "Não moderadores só enxergam alertas ativos; inativos retornam 404.")
    public ResponseEntity<EntityModel<AlertaResponse>> findById(@PathVariable @Positive Long id) {
        boolean moderador = SecurityUtils.currentUsuario().getTipoUsuario() == TipoUsuario.MODERADOR;
        Alerta alerta = moderador ? alertaService.findById(id) : alertaService.findPublicoById(id);
        return ResponseEntity.ok(alertaModelAssembler.toModel(alerta));
    }

    @PreAuthorize("hasRole('MODERADOR')")
    @PostMapping
    @Operation(summary = "Publica o alerta de um caso VALIDADO (o caso passa a PUBLICADO)")
    public ResponseEntity<EntityModel<AlertaResponse>> create(@RequestBody @Valid AlertaRequest request) {
        Alerta created = alertaService.create(request.casoId(), request.titulo(), request.descricaoPublica());
        return ResponseEntity.created(URI.create("/api/alertas/" + created.getId()))
                .body(alertaModelAssembler.toModel(created));
    }

    @PreAuthorize("hasRole('MODERADOR')")
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza título e descrição pública do alerta")
    public ResponseEntity<EntityModel<AlertaResponse>> update(@PathVariable @Positive Long id,
                                                              @RequestBody @Valid AlertaUpdateRequest request) {
        Alerta updated = alertaService.update(id, request.titulo(), request.descricaoPublica());
        return ResponseEntity.ok(alertaModelAssembler.toModel(updated));
    }

    @PreAuthorize("hasRole('MODERADOR')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Retira o alerta (o caso volta a VALIDADO)")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        alertaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
