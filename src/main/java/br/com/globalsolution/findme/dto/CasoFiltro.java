package br.com.globalsolution.findme.dto;

import br.com.globalsolution.findme.model.StatusCaso;

import java.time.LocalDate;

public record CasoFiltro(StatusCaso status, String nome, Integer idadeMin, Integer idadeMax,
                         LocalDate dataInicio, LocalDate dataFim) {
}
