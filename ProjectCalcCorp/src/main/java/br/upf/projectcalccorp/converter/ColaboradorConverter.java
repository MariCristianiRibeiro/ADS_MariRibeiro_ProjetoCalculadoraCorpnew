package br.upf.projectcalccorp.converter;

import br.upf.projectcalccorp.dao.ColaboradorDAO;
import br.upf.projectcalccorp.model.Colaborador;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(forClass = Colaborador.class, managed = true)
public class ColaboradorConverter implements Converter<Colaborador> {

    @Inject
    private ColaboradorDAO dao;

    @Override
    public Colaborador getAsObject(FacesContext context, UIComponent component, String value) {
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
    public String getAsString(FacesContext context, UIComponent component, Colaborador value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}