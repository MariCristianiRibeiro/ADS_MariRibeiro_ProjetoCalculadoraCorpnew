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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Named("colaboradorBean")
@SessionScoped
public class ColaboradorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ColaboradorDAO dao;

    private Colaborador entidade;
    private List<Colaborador> lista;
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
            System.err.println(">>> Erro ao carregar colaboradores: " + e.getMessage());
        }
    }

    public void carregarColaboradores() {
        carregarLista();
    }

    public void pesquisar() {
        if (termoBusca != null && !termoBusca.trim().isEmpty()) {
            lista = dao.buscarPorMatriculaOuEmail(termoBusca);
        } else {
            carregarLista();
        }
    }

    public void novo() {
        entidade = new Colaborador();
        entidade.setValorHora(new BigDecimal("50.00")); // Valor inicial padrão
    }

    public void prepararNovo() {
        novo();
    }

    public void prepararEdicao(Colaborador item) {
        this.entidade = item;
    }

    public void salvar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (entidade.getId() == null) {
                dao.create(entidade);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Colaborador salvo com sucesso!"));
            } else {
                dao.edit(entidade);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Colaborador atualizado com sucesso!"));
            }
            novo();
            carregarLista();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar", e.getMessage()));
        }
    }

    public void excluir(Colaborador item) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            dao.remove(item);
            carregarLista();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Colaborador removido!"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não é possível remover colaborador com agendamentos vinculados."));
        }
    }

    // Getters e Setters Principais
    public Colaborador getEntidade() { return entidade; }
    public void setEntidade(Colaborador entidade) { this.entidade = entidade; }

    public Colaborador getColaboradorSelecionado() { return entidade; }
    public void setColaboradorSelecionado(Colaborador colaboradorSelecionado) { this.entidade = colaboradorSelecionado; }

    public List<Colaborador> getLista() { return lista; }
    public void setLista(List<Colaborador> lista) { this.lista = lista; }

    public List<Colaborador> getColaboradores() { return lista; }
    public void setColaboradores(List<Colaborador> colaboradores) { this.lista = colaboradores; }

    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    // Conversor JSF para Seleção de Colaboradores nos Formulários
    @FacesConverter(forClass = Colaborador.class)
    public static class ColaboradorConverter implements Converter {
        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if (value == null || value.trim().isEmpty()) return null;
            try {
                ColaboradorBean bean = context.getApplication().evaluateExpressionGet(context, "#{colaboradorBean}", ColaboradorBean.class);
                return bean.dao.find(Long.valueOf(value));
            } catch (Exception e) {
                return null;
            }
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