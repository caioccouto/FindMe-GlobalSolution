package br.com.globalsolution.findme.model;

import br.com.globalsolution.findme.exceptions.InvalidStatusTransitionException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Caso {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 150)
    private String nomeDesaparecido;

    @Setter
    @Column(nullable = false)
    private Integer idade;

    @Setter
    @Column(nullable = false, length = 2000)
    private String descricao;

    @Setter
    @Column(nullable = false, length = 300)
    private String ultimaLocalizacaoConhecida;

    @Setter
    @Column(nullable = false)
    private LocalDate dataDesaparecimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCaso status;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Usuario responsavel;

    public void setStatus(StatusCaso novo) {
        if (this.status == null) {
            if (novo != StatusCaso.EM_ANALISE) {
                throw new InvalidStatusTransitionException("Todo caso deve iniciar com o status EM_ANALISE");
            }
            this.status = novo;
            return;
        }
        if (!this.status.podeTransitarPara(novo)) {
            throw new InvalidStatusTransitionException(
                    "Transição inválida de " + this.status + " para " + novo
                            + ". Permitidas: " + this.status.proximosPermitidos());
        }
        this.status = novo;
    }

    public void retirarPublicacao() {
        if (this.status != StatusCaso.PUBLICADO && this.status != StatusCaso.EM_ACOMPANHAMENTO) {
            throw new InvalidStatusTransitionException("Somente casos publicados podem ter o alerta retirado");
        }
        this.status = StatusCaso.VALIDADO;
    }
}
