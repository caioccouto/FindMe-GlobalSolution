package br.com.globalsolution.findme.dto;

import br.com.globalsolution.findme.model.StatusCaso;

import java.time.LocalDate;

public record CasoResponse(Long id, String nomeDesaparecido, Integer idade, String descricao,
                           String ultimaLocalizacaoConhecida, LocalDate dataDesaparecimento,
                           StatusCaso status, Long responsavelId) {
}
