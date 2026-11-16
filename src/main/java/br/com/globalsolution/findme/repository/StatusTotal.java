package br.com.globalsolution.findme.repository;

import br.com.globalsolution.findme.model.StatusCaso;

public interface StatusTotal {

    StatusCaso getStatus();

    Long getTotal();
}
