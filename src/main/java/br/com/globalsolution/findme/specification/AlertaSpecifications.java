package br.com.globalsolution.findme.specification;

import br.com.globalsolution.findme.model.Alerta;
import br.com.globalsolution.findme.dto.AlertaFiltro;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class AlertaSpecifications {

    private AlertaSpecifications() {
    }

    public static Specification<Alerta> filtrar(AlertaFiltro filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            if (filtro.ativo() != null) {
                predicados.add(cb.equal(root.get("ativo"), filtro.ativo()));
            }
            if (filtro.nome() != null && !filtro.nome().isBlank()) {
                predicados.add(cb.like(cb.lower(root.get("caso").get("nomeDesaparecido")),
                        "%" + filtro.nome().trim().toLowerCase() + "%"));
            }
            if (filtro.idadeMin() != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("caso").get("idade"), filtro.idadeMin()));
            }
            if (filtro.idadeMax() != null) {
                predicados.add(cb.lessThanOrEqualTo(root.get("caso").get("idade"), filtro.idadeMax()));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }
}
