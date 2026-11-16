package br.com.globalsolution.findme.repository;

import br.com.globalsolution.findme.model.Caso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CasoRepository extends JpaRepository<Caso, Long>, JpaSpecificationExecutor<Caso> {

    Page<Caso> findByResponsavelId(Long responsavelId, Pageable pageable);

    @Query("select c.status as status, count(c) as total from Caso c group by c.status order by c.status")
    List<StatusTotal> contarPorStatus();
}
