package br.com.globalsolution.findme.mapper;

import br.com.globalsolution.findme.model.Alerta;
import br.com.globalsolution.findme.dto.AlertaResponse;

public final class AlertaMapper {

    private AlertaMapper() {
    }

    public static AlertaResponse toResponse(Alerta alerta) {
        return new AlertaResponse(
                alerta.getId(),
                alerta.getCaso().getId(),
                alerta.getTitulo(),
                alerta.getDescricaoPublica(),
                alerta.getDataPublicacao(),
                alerta.getAtivo(),
                alerta.getCaso().getNomeDesaparecido(),
                alerta.getCaso().getIdade(),
                alerta.getCaso().getDataDesaparecimento()
        );
    }
}
