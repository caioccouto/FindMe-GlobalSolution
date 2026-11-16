package br.com.globalsolution.findme.service;

import br.com.globalsolution.findme.model.Caso;
import br.com.globalsolution.findme.model.StatusCaso;
import br.com.globalsolution.findme.dto.CasoFiltro;
import br.com.globalsolution.findme.dto.StatusTotalResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CasoService {

    Page<Caso> findAll(CasoFiltro filtro, Integer page, Integer size, String sortBy, String direction);

    Page<Caso> findMeus(Integer page, Integer size, String sortBy, String direction);

    Caso findById(Long id);

    Caso create(Caso caso);

    Caso update(Long id, Caso updated);

    Caso changeStatus(Long id, StatusCaso status);

    Caso publish(Long id);

    Caso unpublish(Long id);

    void delete(Long id);

    List<StatusTotalResponse> contarPorStatus();
}
