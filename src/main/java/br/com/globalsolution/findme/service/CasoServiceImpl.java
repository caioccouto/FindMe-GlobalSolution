package br.com.globalsolution.findme.service;

import br.com.globalsolution.findme.configurations.CacheConfig;
import br.com.globalsolution.findme.exceptions.CasoNotEditableException;
import br.com.globalsolution.findme.exceptions.ResourceNotFoundException;
import br.com.globalsolution.findme.model.*;
import br.com.globalsolution.findme.repository.AlertaRepository;
import br.com.globalsolution.findme.repository.CasoRepository;
import br.com.globalsolution.findme.security.SecurityUtils;
import br.com.globalsolution.findme.specification.CasoSpecifications;
import br.com.globalsolution.findme.dto.CasoFiltro;
import br.com.globalsolution.findme.dto.StatusTotalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class CasoServiceImpl implements CasoService {

    private static final Set<String> SORT_FIELDS =
            Set.of("id", "nomeDesaparecido", "idade", "dataDesaparecimento", "status");

    private final CasoRepository casoRepository;
    private final AlertaRepository alertaRepository;

    public CasoServiceImpl(CasoRepository casoRepository, AlertaRepository alertaRepository) {
        this.casoRepository = casoRepository;
        this.alertaRepository = alertaRepository;
    }

    @Override
    public Page<Caso> findAll(CasoFiltro filtro, Integer page, Integer size, String sortBy, String direction) {
        Usuario current = SecurityUtils.currentUsuario();
        Long responsavelId = current.getTipoUsuario() == TipoUsuario.MODERADOR ? null : current.getId();
        Pageable pageable = PageableFactory.of(page, size, sortBy, direction, SORT_FIELDS, "id");
        return casoRepository.findAll(CasoSpecifications.filtrar(responsavelId, filtro), pageable);
    }

    @Override
    public Page<Caso> findMeus(Integer page, Integer size, String sortBy, String direction) {
        Pageable pageable = PageableFactory.of(page, size, sortBy, direction, SORT_FIELDS, "id");
        return casoRepository.findByResponsavelId(SecurityUtils.currentUsuario().getId(), pageable);
    }

    @Override
    public Caso findById(Long id) {
        Caso caso = casoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caso", id));
        Usuario current = SecurityUtils.currentUsuario();
        boolean moderador = current.getTipoUsuario() == TipoUsuario.MODERADOR;
        boolean dono = caso.getResponsavel().getId().equals(current.getId());
        if (!moderador && !dono) {
            throw new AccessDeniedException("Você só pode acessar os seus próprios casos");
        }
        return caso;
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CASOS_ESTATISTICAS, allEntries = true)
    public Caso create(Caso caso) {
        caso.setId(null);
        caso.setResponsavel(SecurityUtils.currentUsuario());
        caso.setStatus(StatusCaso.EM_ANALISE);
        return casoRepository.save(caso);
    }

    @Override
    public Caso update(Long id, Caso updated) {
        Caso existing = findById(id);
        boolean moderador = SecurityUtils.currentUsuario().getTipoUsuario() == TipoUsuario.MODERADOR;
        if (!moderador && existing.getStatus() != StatusCaso.EM_ANALISE) {
            throw new CasoNotEditableException(id);
        }
        existing.setNomeDesaparecido(updated.getNomeDesaparecido());
        existing.setIdade(updated.getIdade());
        existing.setDescricao(updated.getDescricao());
        existing.setUltimaLocalizacaoConhecida(updated.getUltimaLocalizacaoConhecida());
        existing.setDataDesaparecimento(updated.getDataDesaparecimento());
        return casoRepository.save(existing);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {CacheConfig.ALERTAS_PUBLICOS, CacheConfig.ALERTA_PUBLICO,
            CacheConfig.ALERTAS_RECENTES, CacheConfig.CASOS_ESTATISTICAS}, allEntries = true)
    public Caso changeStatus(Long id, StatusCaso status) {
        if (status == StatusCaso.PUBLICADO) {
            throw new IllegalArgumentException(
                    "Um caso só é publicado ao criar o alerta público (POST /api/alertas)");
        }
        Caso caso = findById(id);
        StatusCaso anterior = caso.getStatus();
        caso.setStatus(status);
        Caso saved = casoRepository.save(caso);

        if (status == StatusCaso.LOCALIZADO || status == StatusCaso.ENCERRADO) {
            alertaRepository.findByCasoId(id).ifPresent(alerta -> {
                alerta.setAtivo(false);
                alertaRepository.save(alerta);
            });
        }
        log.info("AUDITORIA caso={} status {} -> {} por {}", id, anterior, status,
                SecurityUtils.currentUsuario().getEmail());
        return saved;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.CASOS_ESTATISTICAS, allEntries = true)
    public Caso publish(Long id) {
        Caso caso = findById(id);
        StatusCaso anterior = caso.getStatus();
        caso.setStatus(StatusCaso.PUBLICADO);
        log.info("AUDITORIA caso={} status {} -> {} por {}", id, anterior, StatusCaso.PUBLICADO,
                SecurityUtils.currentUsuario().getEmail());
        return casoRepository.save(caso);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.CASOS_ESTATISTICAS, allEntries = true)
    public Caso unpublish(Long id) {
        Caso caso = findById(id);
        if (caso.getStatus() == StatusCaso.PUBLICADO || caso.getStatus() == StatusCaso.EM_ACOMPANHAMENTO) {
            StatusCaso anterior = caso.getStatus();
            caso.retirarPublicacao();
            log.info("AUDITORIA caso={} status {} -> {} por {}", id, anterior, StatusCaso.VALIDADO,
                    SecurityUtils.currentUsuario().getEmail());
            return casoRepository.save(caso);
        }
        return caso;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {CacheConfig.ALERTAS_PUBLICOS, CacheConfig.ALERTA_PUBLICO,
            CacheConfig.ALERTAS_RECENTES, CacheConfig.CASOS_ESTATISTICAS}, allEntries = true)
    public void delete(Long id) {
        Caso caso = findById(id);
        alertaRepository.findByCasoId(id).ifPresent((Alerta alerta) -> alertaRepository.delete(alerta));
        casoRepository.delete(caso);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CASOS_ESTATISTICAS)
    public List<StatusTotalResponse> contarPorStatus() {
        return casoRepository.contarPorStatus().stream()
                .map(s -> new StatusTotalResponse(s.getStatus(), s.getTotal()))
                .toList();
    }
}
