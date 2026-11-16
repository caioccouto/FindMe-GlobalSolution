package br.com.globalsolution.findme.model;

import java.util.EnumSet;
import java.util.Set;

public enum StatusCaso {
    EM_ANALISE,
    VALIDADO,
    PUBLICADO,
    EM_ACOMPANHAMENTO,
    LOCALIZADO,
    ENCERRADO;

    public Set<StatusCaso> proximosPermitidos() {
        return switch (this) {
            case EM_ANALISE -> EnumSet.of(VALIDADO, ENCERRADO);
            case VALIDADO -> EnumSet.of(PUBLICADO, ENCERRADO);
            case PUBLICADO -> EnumSet.of(EM_ACOMPANHAMENTO, LOCALIZADO, ENCERRADO);
            case EM_ACOMPANHAMENTO -> EnumSet.of(LOCALIZADO, ENCERRADO);
            case LOCALIZADO -> EnumSet.of(ENCERRADO);
            case ENCERRADO -> EnumSet.noneOf(StatusCaso.class);
        };
    }

    public boolean podeTransitarPara(StatusCaso destino) {
        return proximosPermitidos().contains(destino);
    }
}
