package br.com.globalsolution.findme.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String email) {
        super("Já existe um usuário cadastrado com o email " + email);
    }
}
