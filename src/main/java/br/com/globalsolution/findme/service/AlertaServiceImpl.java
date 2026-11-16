package br.com.globalsolution.findme.service;

import br.com.globalsolution.findme.configurations.CacheConfig;
import br.com.globalsolution.findme.exceptions.AlertaAlreadyExistsException;
import br.com.globalsolution.findme.exceptions.ResourceNotFoundException;
import br.com.globalsolution.findme.model.Alerta;
import br.com.globalsolution.findme.model.Caso;
import br.com.globalsolution.findme.repository.AlertaRepository;
import br.com.globalsolution.findme.specification.AlertaSpecifications;
import br.com.globalsolution.findme.dto.AlertaFiltro;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AlertaServiceImpl implements AlertaService {

    private static final Set<String> SORT_FIELDS =
            Set.of("id", "titulo", "dataPublicacao", "caso.nomeDesaparecido", "caso.idade");

    private final AlertaRepository alertaRepository;
    private final CasoService casoService;

    public AlertaServiceImpl(AlertaRepository alertaRepository, CasoService casoService) {
        this.alertaRepository = alertaRepository;
        this.casoService = casoService;
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.ALERTAS_PUBLICOS)
    public Page<Alerta> findPublicos(AlertaFiltro filtro, Integer page, Integer size, String sortBy, String direction) {
        Pageable pageable = PageableFactory.of(page, size, sortBy, direction, SORT_FIELDS, "dataPublicacao");
        AlertaFiltro apenasAtivos = new AlertaFiltro(filtro.nome(), filtro.idadeMin(), filtro.idadeMax(), true);
        return alertaRepository.findAll(AlertaSpecifications.filtrar(apenasAtivos), pageable);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.ALERTAS_RECENTES)
    public Page<Alerta> findRecentes(Integer dias, Integer page, Integer size, String sortBy, String direction) {
        if (dias == null || dias < 1 || dias > 365) {
            throw new IllegalArgumentException("dias deve estar entre 1 e 365");
        }
        Pageable pageable = PageableFactory.of(page, size, sortBy, direction, SORT_FIELDS, "dataPublicacao");
        return alertaRepository.buscarAtivosPublicadosDesde(LocalDateTime.now().minusDays(dias), pageable);
    }

    @Override
    public Page<Alerta> findAll(AlertaFiltro filtro, Integer page, Integer size, String sortBy, String direction) {
        Pageable pageable = PageableFactory.of(page, size, sortBy, direction, SORT_FIELDS, "dataPublicacao");
        return alertaRepository.findAll(AlertaSpecifications.filtrar(filtro), pageable);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.ALERTA_PUBLICO, key = "#id")
    public Alerta findPublicoById(Long id) {
        Alerta alerta = findById(id);
        if (!Boolean.TRUE.equals(alerta.getAtivo())) {
            throw new ResourceNotFoundException("Alerta", id);
        }
        return alerta;
    }

    @Override
    public Alerta findById(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta", id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {CacheConfig.ALERTAS_PUBLICOS, CacheConfig.ALERTA_PUBLICO,
            CacheConfig.ALERTAS_RECENTES}, allEntries = true)
    public Alerta create(Long casoId, String titulo, String descricaoPublica) {
        if (alertaRepository.existsByCasoId(casoId)) {
            throw new AlertaAlreadyExistsException(casoId);
        }
        Caso caso = casoService.publish(casoId);
        Alerta alerta = new Alerta();
        alerta.setCaso(caso);
        alerta.setTitulo(titulo);
        alerta.setDescricaoPublica(descricaoPublica);
        alerta.setDataPublicacao(LocalDateTime.now());
        alerta.setAtivo(true);
        return alertaRepository.save(alerta);
    }

    @Override
    @CacheEvict(cacheNames = {CacheConfig.ALERTAS_PUBLICOS, CacheConfig.ALERTA_PUBLICO,
            CacheConfig.ALERTAS_RECENTES}, allEntries = true)
    public Alerta update(Long id, String titulo, String descricaoPublica) {
        Alerta alerta = findById(id);
        alerta.setTitulo(titulo);
        alerta.setDescricaoPublica(descricaoPublica);
        return alertaRepository.save(alerta);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {CacheConfig.ALERTAS_PUBLICOS, CacheConfig.ALERTA_PUBLICO,
            CacheConfig.ALERTAS_RECENTES}, allEntries = true)
    public void delete(Long id) {
        Alerta alerta = findById(id);
        Long casoId = alerta.getCaso().getId();
        alertaRepository.delete(alerta);
        casoService.unpublish(casoId);
    }
}
