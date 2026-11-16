package br.com.globalsolution.findme.specification;

import br.com.globalsolution.findme.model.Caso;
import br.com.globalsolution.findme.dto.CasoFiltro;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CasoSpecifications {

    private CasoSpecifications() {
    }

    public static Specification<Caso> filtrar(Long responsavelId, CasoFiltro filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            if (responsavelId != null) {
                predicados.add(cb.equal(root.get("responsavel").get("id"), responsavelId));
            }
            if (filtro.status() != null) {
                predicados.add(cb.equal(root.get("status"), filtro.status()));
            }
            if (filtro.nome() != null && !filtro.nome().isBlank()) {
                predicados.add(cb.like(cb.lower(root.get("nomeDesaparecido")),
                        "%" + filtro.nome().trim().toLowerCase() + "%"));
            }
            if (filtro.idadeMin() != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("idade"), filtro.idadeMin()));
            }
            if (filtro.idadeMax() != null) {
                predicados.add(cb.lessThanOrEqualTo(root.get("idade"), filtro.idadeMax()));
            }
            if (filtro.dataInicio() != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("dataDesaparecimento"), filtro.dataInicio()));
            }
            if (filtro.dataFim() != null) {
                predicados.add(cb.lessThanOrEqualTo(root.get("dataDesaparecimento"), filtro.dataFim()));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }
}
