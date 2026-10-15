/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.upf.projectcalccorp.bean;

import br.upf.projectcalccorp.dao.SalaReuniaoDAO;
import br.upf.projectcalccorp.model.SalaReuniao;
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

@Named("salaReuniaoBean")
@SessionScoped
public class SalaReuniaoBean implements Serializable {

    @EJB
    private SalaReuniaoDAO dao;

    private SalaReuniao entidade;
    private List<SalaReuniao> lista;
    private String termoBusca;

    @PostConstruct
    public void init() {
        entidade = new SalaReuniao();
        carregarLista();
    }

    public void carregarLista() {
        lista = dao.findAll();
    }

    public void pesquisar() {
        lista = dao.buscarPorNome(termoBusca);
    }

    public void novo() {
        entidade = new SalaReuniao();
    }

    public void salvar() {
        if (entidade.getId() == null) {
            dao.create(entidade);
        } else {
            dao.edit(entidade);
        }
        entidade = new SalaReuniao();
        carregarLista();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Sala salva com sucesso!"));
    }

    public void excluir(SalaReuniao item) {
        dao.remove(item);
        carregarLista();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Sala removida!"));
    }

    public SalaReuniao getEntidade() { return entidade; }
    public void setEntidade(SalaReuniao entidade) { this.entidade = entidade; }
    public List<SalaReuniao> getLista() { return lista; }
    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    @FacesConverter(forClass = SalaReuniao.class)
    public static class SalaConverter implements Converter {
        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if (value == null || value.trim().isEmpty()) return null;
            SalaReuniaoBean bean = context.getApplication().evaluateExpressionGet(context, "#{salaReuniaoBean}", SalaReuniaoBean.class);
            return bean.dao.find(Long.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value == null) return "";
            if (value instanceof SalaReuniao) {
                SalaReuniao e = (SalaReuniao) value;
                return e.getId() != null ? e.getId().toString() : "";
            }
            return "";
        }
    }
}
