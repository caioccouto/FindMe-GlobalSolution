package br.com.globalsolution.findme.exceptions;

public class CasoNotEditableException extends RuntimeException {

    public CasoNotEditableException(Long id) {
        super("O caso com id " + id + " não pode mais ser editado pelo responsável, pois já passou da fase EM_ANALISE");
    }
}
