package br.com.globalsolution.findme.controller;

import br.com.globalsolution.findme.assembler.CasoModelAssembler;
import br.com.globalsolution.findme.model.Caso;
import br.com.globalsolution.findme.model.StatusCaso;
import br.com.globalsolution.findme.service.CasoService;
import br.com.globalsolution.findme.dto.*;
import br.com.globalsolution.findme.mapper.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/casos")
@Validated
@Tag(name = "Casos", description = "Casos de desaparecimento (dados privados: apenas responsável dono e moderador)")
public class CasoController {

    private final CasoService casoService;
    private final CasoModelAssembler casoModelAssembler;

    public CasoController(CasoService casoService, CasoModelAssembler casoModelAssembler) {
        this.casoService = casoService;
        this.casoModelAssembler = casoModelAssembler;
    }

    @PreAuthorize("hasAnyRole('RESPONSAVEL','MODERADOR')")
    @GetMapping
    @Operation(summary = "Lista casos com filtros dinâmicos, paginação e ordenação",
            description = "MODERADOR vê todos; RESPONSAVEL vê apenas os próprios. sortBy: id, nomeDesaparecido, idade, dataDesaparecimento, status.")
    @ApiResponse(responseCode = "200", description = "Página de casos")
    @ApiResponse(responseCode = "403", description = "Perfil sem permissão",
            content = @Content(schema = @Schema(implementation = ErrorCustomResponse.class)))
    public PageResponse<EntityModel<CasoResponse>> findAll(
            @RequestParam(required = false) StatusCaso status,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) @PositiveOrZero Integer idadeMin,
            @RequestParam(required = false) @PositiveOrZero Integer idadeMax,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer sizePage,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction) {
        CasoFiltro filtro = new CasoFiltro(status, nome, idadeMin, idadeMax, dataInicio, dataFim);
        Page<Caso> pageResult = casoService.findAll(filtro, page, sizePage, sortBy, direction);
        return PageResponse.from(pageResult.map(casoModelAssembler::toModel));
    }

    @PreAuthorize("hasRole('RESPONSAVEL')")
    @GetMapping("/meus")
    @Operation(summary = "Lista os casos do responsável autenticado")
    public PageResponse<EntityModel<CasoResponse>> findMeus(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer sizePage,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction) {
        return PageResponse.from(casoService.findMeus(page, sizePage, sortBy, direction)
                .map(casoModelAssembler::toModel));
    }

    @PreAuthorize("hasRole('MODERADOR')")
    @GetMapping("/estatisticas")
    @Operation(summary = "Total de casos por status (JPQL com cache)")
    public List<StatusTotalResponse> estatisticas() {
        return casoService.contarPorStatus();
    }

    @PreAuthorize("hasAnyRole('RESPONSAVEL','MODERADOR')")
    @GetMapping("/{id}")
    @Operation(summary = "Busca caso por id")
    public ResponseEntity<EntityModel<CasoResponse>> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(casoModelAssembler.toModel(casoService.findById(id)));
    }

    @PreAuthorize("hasRole('RESPONSAVEL')")
    @PostMapping
    @Operation(summary = "Registra um caso (nasce EM_ANALISE, associado ao responsável autenticado)")
    public ResponseEntity<EntityModel<CasoResponse>> create(@RequestBody @Valid CasoRequest request) {
        Caso created = casoService.create(toEntity(request));
        return ResponseEntity.created(URI.create("/api/casos/" + created.getId()))
                .body(casoModelAssembler.toModel(created));
    }

    @PreAuthorize("hasAnyRole('RESPONSAVEL','MODERADOR')")
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados do caso",
            description = "O responsável só edita o próprio caso enquanto EM_ANALISE; o moderador edita qualquer caso.")
    public ResponseEntity<EntityModel<CasoResponse>> update(@PathVariable @Positive Long id,
                                                            @RequestBody @Valid CasoRequest request) {
        Caso updated = casoService.update(id, toEntity(request));
        return ResponseEntity.ok(casoModelAssembler.toModel(updated));
    }

    @PreAuthorize("hasRole('MODERADOR')")
    @PatchMapping("/{id}/status")
    @Operation(summary = "Altera o status do caso (máquina de estados)",
            description = "PUBLICADO só é atingido ao criar o alerta. LOCALIZADO/ENCERRADO desativam o alerta.")
    public ResponseEntity<EntityModel<CasoResponse>> changeStatus(@PathVariable @Positive Long id,
                                                                  @RequestBody @Valid ChangeStatusRequest request) {
        return ResponseEntity.ok(casoModelAssembler.toModel(casoService.changeStatus(id, request.status())));
    }

    @PreAuthorize("hasRole('MODERADOR')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui o caso (e o alerta associado)")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        casoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Caso toEntity(CasoRequest request) {
        Caso caso = new Caso();
        caso.setNomeDesaparecido(request.nomeDesaparecido());
        caso.setIdade(request.idade());
        caso.setDescricao(request.descricao());
        caso.setUltimaLocalizacaoConhecida(request.ultimaLocalizacaoConhecida());
        caso.setDataDesaparecimento(request.dataDesaparecimento());
        return caso;
    }
}
