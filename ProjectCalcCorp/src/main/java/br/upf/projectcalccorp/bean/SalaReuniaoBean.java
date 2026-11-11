package br.upf.projectcalccorp.bean;

import br.upf.projectcalccorp.dao.SalaReuniaoDAO;
import br.upf.projectcalccorp.model.SalaReuniao;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("salaBean")
@ViewScoped
public class SalaReuniaoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private SalaReuniaoDAO dao;

    private SalaReuniao entidade;
    private List<SalaReuniao> lista;
    private String termoBusca;

    public SalaReuniaoBean() {
    }

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
            System.err.println(">>> Erro ao carregar salas: " + e.getMessage());
        }
    }

    public void carregarSalas() {
        carregarLista();
    }

    public void pesquisar() {
        if (termoBusca != null && !termoBusca.trim().isEmpty()) {
            lista = dao.buscarPorNome(termoBusca);
        } else {
            carregarLista();
        }
    }

    public void novo() {
        entidade = new SalaReuniao();
    }

    public void prepararNovaSala() {
        novo();
    }

    public void prepararEdicao(SalaReuniao item) {
        this.entidade = item;
    }

    public void salvar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (entidade.getId() == null) {
                dao.create(entidade);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Sala salva com sucesso!"));
            } else {
                dao.edit(entidade);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Sala atualizada com sucesso!"));
            }
            novo();
            carregarLista();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao salvar", e.getMessage()));
        }
    }

    public void excluir(SalaReuniao item) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            dao.remove(item);
            carregarLista();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Sala removida com sucesso!"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não é possível remover salas associadas a agendamentos registrados."));
        }
    }

    // Getters e Setters Principais e Aliases para compatibilidade
    public SalaReuniao getEntidade() { return entidade; }
    public void setEntidade(SalaReuniao entidade) { this.entidade = entidade; }

    public SalaReuniao getSalaSelecionada() { return entidade; }
    public void setSalaSelecionada(SalaReuniao salaSelecionada) { this.entidade = salaSelecionada; }

    public List<SalaReuniao> getLista() { return lista; }
    public void setLista(List<SalaReuniao> lista) { this.lista = lista; }

    public List<SalaReuniao> getSalas() { return lista; }
    public void setSalas(List<SalaReuniao> salas) { this.lista = salas; }

    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    // Conversor JSF Integrado
    @FacesConverter(forClass = SalaReuniao.class)
    public static class SalaConverter implements Converter {
        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if (value == null || value.trim().isEmpty()) return null;
            try {
                SalaReuniaoBean bean = context.getApplication().evaluateExpressionGet(context, "#{salaBean}", SalaReuniaoBean.class);
                return bean.dao.find(Long.valueOf(value));
            } catch (Exception e) {
                return null;
            }
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