package br.com.globalsolution.findme.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlertaResponse(Long id, Long casoId, String titulo, String descricaoPublica,
                             LocalDateTime dataPublicacao, Boolean ativo, String nomeDesaparecido,
                             Integer idade, LocalDate dataDesaparecimento) {
}
