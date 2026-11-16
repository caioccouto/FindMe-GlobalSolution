package br.com.globalsolution.findme.assembler;

import br.com.globalsolution.findme.controller.AlertaController;
import br.com.globalsolution.findme.controller.CasoController;
import br.com.globalsolution.findme.mapper.AlertaMapper;
import br.com.globalsolution.findme.model.Alerta;
import br.com.globalsolution.findme.model.TipoUsuario;
import br.com.globalsolution.findme.security.SecurityUtils;
import br.com.globalsolution.findme.dto.AlertaResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AlertaModelAssembler implements RepresentationModelAssembler<Alerta, EntityModel<AlertaResponse>> {

    @Override
    public EntityModel<AlertaResponse> toModel(Alerta alerta) {
        Long id = alerta.getId();
        boolean moderador = SecurityUtils.currentUsuario().getTipoUsuario() == TipoUsuario.MODERADOR;

        EntityModel<AlertaResponse> model = EntityModel.of(AlertaMapper.toResponse(alerta));
        model.add(linkTo(methodOn(AlertaController.class).findById(id)).withSelfRel());
        model.add(linkTo(AlertaController.class).withRel("alertas"));
        model.add(linkTo(AlertaController.class).slash("recentes").withRel("alertas-recentes"));

        if (moderador) {
            model.add(linkTo(methodOn(CasoController.class).findById(alerta.getCaso().getId())).withRel("caso"));
            model.add(linkTo(methodOn(AlertaController.class).update(id, null)).withRel("atualizar"));
            model.add(linkTo(methodOn(AlertaController.class).delete(id)).withRel("retirar"));
        }
        return model;
    }
}
