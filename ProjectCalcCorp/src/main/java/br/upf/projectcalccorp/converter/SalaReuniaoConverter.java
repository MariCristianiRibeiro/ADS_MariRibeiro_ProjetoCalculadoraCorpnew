package br.upf.projectcalccorp.converter;

import br.upf.projectcalccorp.dao.SalaReuniaoDAO;
import br.upf.projectcalccorp.model.SalaReuniao;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(forClass = SalaReuniao.class, managed = true)
public class SalaReuniaoConverter implements Converter<SalaReuniao> {

    @Inject
    private SalaReuniaoDAO dao;

    @Override
    public SalaReuniao getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return dao.find(Long.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, SalaReuniao value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}