package br.com.globalsolution.findme.exceptions;

public class AlertaAlreadyExistsException extends RuntimeException {

    public AlertaAlreadyExistsException(Long casoId) {
        super("O caso com id " + casoId + " já possui um alerta público");
    }
}
