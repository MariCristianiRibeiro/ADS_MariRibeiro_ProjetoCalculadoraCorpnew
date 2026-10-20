package br.upf.projectcalccorp.converter;

import br.upf.projectcalccorp.dao.RecursoTecnologicoDAO;
import br.upf.projectcalccorp.model.RecursoTecnologico;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(forClass = RecursoTecnologico.class, managed = true)
public class RecursoTecnologicoConverter implements Converter<RecursoTecnologico> {

    @Inject
    private RecursoTecnologicoDAO dao;

    @Override
    public RecursoTecnologico getAsObject(FacesContext context, UIComponent component, String value) {
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
    public String getAsString(FacesContext context, UIComponent component, RecursoTecnologico value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}