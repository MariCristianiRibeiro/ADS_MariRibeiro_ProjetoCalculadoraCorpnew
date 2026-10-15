/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.upf.projectcalccorp.bean;

import br.upf.projectcalccorp.dao.AgendamentoReuniaoDAO;
import br.upf.projectcalccorp.model.AgendamentoReuniao;
import br.upf.projectcalccorp.model.StatusAgendamento;
import br.upf.projectcalccorp.model.TipoReuniao;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Named("agendamentoBean")
@SessionScoped
public class AgendamentoBean implements Serializable {

    @EJB
    private AgendamentoReuniaoDAO dao;

    private AgendamentoReuniao entidade;
    private List<AgendamentoReuniao> lista;

    @PostConstruct
    public void init() {
        novo();
        carregarLista();
    }

    public void carregarLista() {
        lista = dao.findAll();
    }

    public void novo() {
        entidade = new AgendamentoReuniao();
        entidade.setStatus(StatusAgendamento.SOLICITADO);
        entidade.setTipo(TipoReuniao.PRESENCIAL);
        entidade.setDuracaoHoras(0.0);
        entidade.setCustoTotal(BigDecimal.ZERO);
    }

    public void calcularCustoEHoras() {
        if (entidade.getDataHoraInicio() != null && entidade.getDataHoraFim() != null) {
            long diffMillis = entidade.getDataHoraFim().getTime() - entidade.getDataHoraInicio().getTime();
            if (diffMillis > 0) {
                double horas = diffMillis / (1000.0 * 60.0 * 60.0);
                entidade.setDuracaoHoras(BigDecimal.valueOf(horas).setScale(2, RoundingMode.HALF_UP).doubleValue());

                if (entidade.getSala() != null && entidade.getSala().getValorHora() != null) {
                    BigDecimal custoSala = entidade.getSala().getValorHora().multiply(BigDecimal.valueOf(horas));
                    int qtdRecursos = (entidade.getRecursos() != null) ? entidade.getRecursos().size() : 0;
                    BigDecimal custoRecursos = BigDecimal.valueOf(qtdRecursos * 20.0);

                    entidade.setCustoTotal(custoSala.add(custoRecursos).setScale(2, RoundingMode.HALF_UP));
                } else {
                    entidade.setCustoTotal(BigDecimal.ZERO);
                }
            } else {
                entidade.setDuracaoHoras(0.0);
                entidade.setCustoTotal(BigDecimal.ZERO);
            }
        }
    }

    public void salvar() {
        try {
            calcularCustoEHoras();
            if (entidade.getDuracaoHoras() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "A data/hora final deve ser maior que a inicial."));
                return;
            }

            if (entidade.getId() == null) {
                dao.create(entidade);
            } else {
                dao.edit(entidade);
            }
            novo();
            carregarLista();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Agendamento concluído com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage()));
        }
    }

    public void excluir(AgendamentoReuniao item) {
        dao.remove(item);
        carregarLista();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Agendamento removido!"));
    }

    public StatusAgendamento[] getListaStatus() {
        return StatusAgendamento.values();
    }

    public TipoReuniao[] getListaTipos() {
        return TipoReuniao.values();
    }

    public AgendamentoReuniao getEntidade() { return entidade; }
    public void setEntidade(AgendamentoReuniao entidade) { this.entidade = entidade; }
    public List<AgendamentoReuniao> getLista() { return lista; }
}
