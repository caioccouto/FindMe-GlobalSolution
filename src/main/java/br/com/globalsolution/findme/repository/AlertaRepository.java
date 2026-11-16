package br.com.globalsolution.findme.repository;

import br.com.globalsolution.findme.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AlertaRepository extends JpaRepository<Alerta, Long>, JpaSpecificationExecutor<Alerta> {

    Optional<Alerta> findByCasoId(Long casoId);

    boolean existsByCasoId(Long casoId);

    @Query("select a from Alerta a where a.ativo = true and a.dataPublicacao >= :desde")
    Page<Alerta> buscarAtivosPublicadosDesde(@Param("desde") LocalDateTime desde, Pageable pageable);
}
