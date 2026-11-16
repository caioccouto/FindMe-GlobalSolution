package br.com.globalsolution.findme.assembler;

import br.com.globalsolution.findme.controller.AlertaController;
import br.com.globalsolution.findme.controller.CasoController;
import br.com.globalsolution.findme.mapper.CasoMapper;
import br.com.globalsolution.findme.model.Caso;
import br.com.globalsolution.findme.model.StatusCaso;
import br.com.globalsolution.findme.model.TipoUsuario;
import br.com.globalsolution.findme.model.Usuario;
import br.com.globalsolution.findme.repository.AlertaRepository;
import br.com.globalsolution.findme.security.SecurityUtils;
import br.com.globalsolution.findme.dto.CasoResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CasoModelAssembler implements RepresentationModelAssembler<Caso, EntityModel<CasoResponse>> {

    private final AlertaRepository alertaRepository;

    public CasoModelAssembler(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Override
    public EntityModel<CasoResponse> toModel(Caso caso) {
        Long id = caso.getId();
        Usuario current = SecurityUtils.currentUsuario();
        boolean moderador = current.getTipoUsuario() == TipoUsuario.MODERADOR;
        boolean dono = caso.getResponsavel().getId().equals(current.getId());

        EntityModel<CasoResponse> model = EntityModel.of(CasoMapper.toResponse(caso));
        model.add(linkTo(methodOn(CasoController.class).findById(id)).withSelfRel());
        model.add(linkTo(CasoController.class).withRel("casos"));
        model.add(linkTo(CasoController.class).slash("meus").withRel("meus-casos"));

        if (caso.getStatus() != StatusCaso.EM_ANALISE && caso.getStatus() != StatusCaso.VALIDADO) {
            alertaRepository.findByCasoId(id).ifPresent(alerta ->
                    model.add(linkTo(methodOn(AlertaController.class).findById(alerta.getId())).withRel("alerta")));
        }

        if (moderador || (dono && caso.getStatus() == StatusCaso.EM_ANALISE)) {
            model.add(linkTo(methodOn(CasoController.class).update(id, null)).withRel("atualizar"));
        }
        if (moderador) {
            model.add(linkTo(methodOn(CasoController.class).changeStatus(id, null)).withRel("alterar-status"));
            model.add(linkTo(methodOn(CasoController.class).delete(id)).withRel("excluir"));
            if (caso.getStatus() == StatusCaso.VALIDADO) {
                model.add(linkTo(methodOn(AlertaController.class).create(null)).withRel("publicar-alerta"));
            }
        }
        return model;
    }
}
