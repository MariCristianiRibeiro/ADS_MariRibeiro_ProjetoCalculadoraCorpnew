/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.upf.projectcalccorp.bean;

import br.upf.projectcalccorp.dao.ColaboradorDAO;
import br.upf.projectcalccorp.model.Colaborador;
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

@Named("colaboradorBean")
@SessionScoped
public class ColaboradorBean implements Serializable {

    @EJB
    private ColaboradorDAO dao;

    private Colaborador entidade;
    private List<Colaborador> lista;
    private String termoBusca;

    @PostConstruct
    public void init() {
        entidade = new Colaborador();
        carregarLista();
    }

    public void carregarLista() {
        lista = dao.findAll();
    }

    public void pesquisar() {
        lista = dao.buscarPorMatriculaOuEmail(termoBusca);
    }

    public void novo() {
        entidade = new Colaborador();
    }

    public void salvar() {
        try {
            if (entidade.getId() == null) {
                dao.create(entidade);
            } else {
                dao.edit(entidade);
            }
            entidade = new Colaborador();
            carregarLista();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Colaborador salvo com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage()));
        }
    }

    public void excluir(Colaborador item) {
        dao.remove(item);
        carregarLista();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Colaborador removido!"));
    }

    public Colaborador getEntidade() { return entidade; }
    public void setEntidade(Colaborador entidade) { this.entidade = entidade; }
    public List<Colaborador> getLista() { return lista; }
    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    @FacesConverter(forClass = Colaborador.class)
    public static class ColaboradorConverter implements Converter {
        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if (value == null || value.trim().isEmpty()) return null;
            ColaboradorBean bean = context.getApplication().evaluateExpressionGet(context, "#{colaboradorBean}", ColaboradorBean.class);
            return bean.dao.find(Long.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value == null) return "";
            if (value instanceof Colaborador) {
                Colaborador e = (Colaborador) value;
                return e.getId() != null ? e.getId().toString() : "";
            }
            return "";
        }
    }
}
