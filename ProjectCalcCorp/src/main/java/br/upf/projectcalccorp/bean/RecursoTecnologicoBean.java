/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.upf.projectcalccorp.bean;

import br.upf.projectcalccorp.dao.RecursoTecnologicoDAO;
import br.upf.projectcalccorp.model.RecursoTecnologico;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("recursoTecnologicoBean")
@SessionScoped
public class RecursoTecnologicoBean implements Serializable {

    @EJB
    private RecursoTecnologicoDAO dao;

    private RecursoTecnologico entidade;
    private List<RecursoTecnologico> lista;
    private String termoBusca;

    @PostConstruct
    public void init() {
        entidade = new RecursoTecnologico();
        carregarLista();
    }

    public void carregarLista() {
        lista = dao.findAll();
    }

    public void pesquisar() {
        lista = dao.buscarPorNome(termoBusca);
    }

    public void novo() {
        entidade = new RecursoTecnologico();
    }

    public void salvar() {
        if (entidade.getId() == null) {
            dao.create(entidade);
        } else {
            dao.edit(entidade);
        }
        entidade = new RecursoTecnologico();
        carregarLista();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Recurso salvo com sucesso!"));
    }

    public void excluir(RecursoTecnologico item) {
        dao.remove(item);
        carregarLista();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Recurso removido!"));
    }

    public RecursoTecnologico getEntidade() { return entidade; }
    public void setEntidade(RecursoTecnologico entidade) { this.entidade = entidade; }
    public List<RecursoTecnologico> getLista() { return lista; }
    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    @FacesConverter(forClass = RecursoTecnologico.class)
    public static class RecursoConverter implements Converter {
        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if (value == null || value.trim().isEmpty()) return null;
            RecursoTecnologicoBean bean = context.getApplication().evaluateExpressionGet(context, "#{recursoTecnologicoBean}", RecursoTecnologicoBean.class);
            return bean.dao.find(Long.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value == null) return "";
            if (value instanceof RecursoTecnologico) {
                RecursoTecnologico e = (RecursoTecnologico) value;
                return e.getId() != null ? e.getId().toString() : "";
            }
            return "";
        }
    }
}
