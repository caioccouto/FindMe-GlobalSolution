package br.com.globalsolution.findme.dto;

import br.com.globalsolution.findme.model.StatusCaso;

public record StatusTotalResponse(StatusCaso status, Long total) {
}
