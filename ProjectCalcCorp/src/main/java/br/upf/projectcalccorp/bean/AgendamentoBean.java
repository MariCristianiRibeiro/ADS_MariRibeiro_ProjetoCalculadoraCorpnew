package br.upf.projectcalccorp.bean;

import br.upf.projectcalccorp.dao.AgendamentoReuniaoDAO;
import br.upf.projectcalccorp.dao.RecursoTecnologicoDAO;
import br.upf.projectcalccorp.dao.SalaReuniaoDAO;
import br.upf.projectcalccorp.model.AgendamentoReuniao;
import br.upf.projectcalccorp.model.Colaborador;
import br.upf.projectcalccorp.model.RecursoTecnologico;
import br.upf.projectcalccorp.model.SalaReuniao;
import br.upf.projectcalccorp.model.StatusAgendamento;
import br.upf.projectcalccorp.model.TipoReuniao;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("agendamentoBean")
@RequestScoped
public class AgendamentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AgendamentoReuniaoDAO agendamentoReuniaoDAO;

    @EJB
    private SalaReuniaoDAO salaReuniaoDAO;

    @EJB
    private RecursoTecnologicoDAO recursoTecnologicoDAO;

    private List<AgendamentoReuniao> agendamentos;
    private List<SalaReuniao> salas;
    private List<RecursoTecnologico> recursosDisponiveis;

    private AgendamentoReuniao novoAgendamento;
    private Long idSalaSelecionada;

    public AgendamentoBean() {
    }

    @PostConstruct
    public void init() {
        carregarDados();
        prepararNovoAgendamento();
    }

    public void carregarDados() {
        try {
            agendamentos = (agendamentoReuniaoDAO != null) ? agendamentoReuniaoDAO.findAll() : new ArrayList<>();
            salas = (salaReuniaoDAO != null) ? salaReuniaoDAO.findAll() : new ArrayList<>();
            recursosDisponiveis = (recursoTecnologicoDAO != null) ? recursoTecnologicoDAO.findAll() : new ArrayList<>();
        } catch (Exception e) {
            System.err.println(">>> Erro ao carregar dados do banco: " + e.getMessage());
            agendamentos = new ArrayList<>();
            salas = new ArrayList<>();
            recursosDisponiveis = new ArrayList<>();
        }
    }

    public void prepararNovoAgendamento() {
        novoAgendamento = new AgendamentoReuniao();
        novoAgendamento.setStatus(StatusAgendamento.AGENDADO);
        novoAgendamento.setTipo(TipoReuniao.INTERNA);
        novoAgendamento.setDataHoraInicio(new Date());
        novoAgendamento.setDuracaoHoras(1.0);
        novoAgendamento.setCustoTotal(BigDecimal.ZERO);
        idSalaSelecionada = null;
    }

    public void calcularCustoTotal() {
        if (idSalaSelecionada != null && novoAgendamento.getDuracaoHoras() != null && salaReuniaoDAO != null) {
            SalaReuniao sala = salaReuniaoDAO.find(idSalaSelecionada);
            if (sala != null && sala.getValorHora() != null) {
                BigDecimal duracao = BigDecimal.valueOf(novoAgendamento.getDuracaoHoras());
                novoAgendamento.setCustoTotal(sala.getValorHora().multiply(duracao));
            }
        }
    }

    public void salvar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            Colaborador usuarioLogado = (Colaborador) session.getAttribute("usuarioLogado");

            if (usuarioLogado == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Sessão expirada. Faça login novamente."));
                return;
            }

            if (idSalaSelecionada == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Selecione uma sala de reunião."));
                return;
            }

            SalaReuniao sala = salaReuniaoDAO.find(idSalaSelecionada);
            novoAgendamento.setSala(sala);
            novoAgendamento.setSolicitante(usuarioLogado);

            // Calcula a data e hora de término com base nas horas informadas
            long inicioMs = novoAgendamento.getDataHoraInicio().getTime();
            long duracaoMs = (long) (novoAgendamento.getDuracaoHoras() * 3600000);
            novoAgendamento.setDataHoraFim(new Date(inicioMs + duracaoMs));

            calcularCustoTotal();

            agendamentoReuniaoDAO.create(novoAgendamento);
            carregarDados();
            prepararNovoAgendamento();

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Agendamento criado com sucesso!"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao salvar agendamento: " + e.getMessage()));
        }
    }

    // Getters e Setters
    public List<AgendamentoReuniao> getAgendamentos() { return agendamentos; }
    public void setAgendamentos(List<AgendamentoReuniao> agendamentos) { this.agendamentos = agendamentos; }

    public List<SalaReuniao> getSalas() { return salas; }
    public void setSalas(List<SalaReuniao> salas) { this.salas = salas; }

    public List<RecursoTecnologico> getRecursosDisponiveis() { return recursosDisponiveis; }
    public void setRecursosDisponiveis(List<RecursoTecnologico> recursosDisponiveis) { this.recursosDisponiveis = recursosDisponiveis; }

    public AgendamentoReuniao getNovoAgendamento() { return novoAgendamento; }
    public void setNovoAgendamento(AgendamentoReuniao novoAgendamento) { this.novoAgendamento = novoAgendamento; }

    public Long getIdSalaSelecionada() { return idSalaSelecionada; }
    public void setIdSalaSelecionada(Long idSalaSelecionada) { this.idSalaSelecionada = idSalaSelecionada; }

    public StatusAgendamento[] getStatusAgendamentoValues() { return StatusAgendamento.values(); }
    public TipoReuniao[] getTiposReuniao() { return TipoReuniao.values(); }
}