package br.com.globalsolution.findme.service;

import br.com.globalsolution.findme.model.Alerta;
import br.com.globalsolution.findme.dto.AlertaFiltro;
import org.springframework.data.domain.Page;

public interface AlertaService {

    Page<Alerta> findPublicos(AlertaFiltro filtro, Integer page, Integer size, String sortBy, String direction);

    Page<Alerta> findRecentes(Integer dias, Integer page, Integer size, String sortBy, String direction);

    Page<Alerta> findAll(AlertaFiltro filtro, Integer page, Integer size, String sortBy, String direction);

    Alerta findPublicoById(Long id);

    Alerta findById(Long id);

    Alerta create(Long casoId, String titulo, String descricaoPublica);

    Alerta update(Long id, String titulo, String descricaoPublica);

    void delete(Long id);
}
