package br.com.globalsolution.findme.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableFactory {

    private PageableFactory() {
    }

    public static Pageable of(Integer page, Integer size, String sortBy, String direction,
                              Set<String> allowedSortFields, String defaultSortField) {
        if (page == null || page < 0) {
            throw new IllegalArgumentException("page deve ser maior ou igual a 0");
        }
        if (size == null || size < 1 || size > 100) {
            throw new IllegalArgumentException("sizePage deve estar entre 1 e 100");
        }
        String field = (sortBy == null || sortBy.isBlank()) ? defaultSortField : sortBy;
        if (!allowedSortFields.contains(field)) {
            throw new IllegalArgumentException("sortBy inválido: " + field + ". Valores aceitos: " + allowedSortFields);
        }
        Sort.Direction dir;
        if (direction == null || direction.isBlank() || direction.equalsIgnoreCase("asc")) {
            dir = Sort.Direction.ASC;
        } else if (direction.equalsIgnoreCase("desc")) {
            dir = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("direction inválida: use asc ou desc");
        }
        return PageRequest.of(page, size, Sort.by(dir, field));
    }
}
