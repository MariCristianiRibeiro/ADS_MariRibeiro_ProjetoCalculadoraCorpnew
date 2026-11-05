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
import java.util.ArrayList;
import java.util.List;

@Named("recursoTecnologicoBean")
@SessionScoped
public class RecursoTecnologicoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private RecursoTecnologicoDAO dao;

    private RecursoTecnologico entidade;
    private List<RecursoTecnologico> lista;
    private String termoBusca;

    @PostConstruct
    public void init() {
        novo();
        carregarLista();
    }

    public void carregarLista() {
        try {
            lista = (dao != null) ? dao.findAll() : new ArrayList<>();
        } catch (Exception e) {
            lista = new ArrayList<>();
            System.err.println(">>> Erro ao carregar recursos: " + e.getMessage());
        }
    }

    public void pesquisar() {
        if (termoBusca != null && !termoBusca.trim().isEmpty()) {
            lista = dao.buscarPorNome(termoBusca);
        } else {
            carregarLista();
        }
    }

    public void novo() {
        entidade = new RecursoTecnologico();
    }

    public void prepararEdicao(RecursoTecnologico item) {
        this.entidade = item;
    }

    public void salvar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (entidade.getId() == null) {
                dao.create(entidade);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Recurso salvo com sucesso!"));
            } else {
                dao.edit(entidade);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Recurso atualizado com sucesso!"));
            }
            novo();
            carregarLista();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar", e.getMessage()));
        }
    }

    public void excluir(RecursoTecnologico item) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            dao.remove(item);
            carregarLista();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Recurso removido com sucesso!"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não é possível remover recursos associados a reuniões registradas."));
        }
    }

    // Getters e Setters
    public RecursoTecnologico getEntidade() { return entidade; }
    public void setEntidade(RecursoTecnologico entidade) { this.entidade = entidade; }

    public List<RecursoTecnologico> getLista() { return lista; }
    public void setLista(List<RecursoTecnologico> lista) { this.lista = lista; }

    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    // Conversor JSF
    @FacesConverter(forClass = RecursoTecnologico.class)
    public static class RecursoConverter implements Converter {
        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if (value == null || value.trim().isEmpty()) return null;
            try {
                RecursoTecnologicoBean bean = context.getApplication().evaluateExpressionGet(context, "#{recursoTecnologicoBean}", RecursoTecnologicoBean.class);
                return bean.dao.find(Long.valueOf(value));
            } catch (Exception e) {
                return null;
            }
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