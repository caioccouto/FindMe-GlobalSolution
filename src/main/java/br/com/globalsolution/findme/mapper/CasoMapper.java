package br.com.globalsolution.findme.mapper;

import br.com.globalsolution.findme.model.Caso;
import br.com.globalsolution.findme.dto.CasoResponse;

public final class CasoMapper {

    private CasoMapper() {
    }

    public static CasoResponse toResponse(Caso caso) {
        return new CasoResponse(
                caso.getId(),
                caso.getNomeDesaparecido(),
                caso.getIdade(),
                caso.getDescricao(),
                caso.getUltimaLocalizacaoConhecida(),
                caso.getDataDesaparecimento(),
                caso.getStatus(),
                caso.getResponsavel().getId()
        );
    }
}
