package br.upf.projectcalccorp.bean;

import br.upf.projectcalccorp.dao.AgendamentoReuniaoDAO;
import br.upf.projectcalccorp.dao.ColaboradorDAO;
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

    @EJB
    private ColaboradorDAO colaboradorDAO;

    private List<AgendamentoReuniao> agendamentos;
    private List<SalaReuniao> salas;
    private List<RecursoTecnologico> recursosDisponiveis;
    private List<Colaborador> colaboradoresDisponiveis;

    private AgendamentoReuniao novoAgendamento;
    private Long idSalaSelecionada;
    private List<Long> idsParticipantesSelecionados;
    private boolean alertaEmail;

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
            colaboradoresDisponiveis = (colaboradorDAO != null) ? colaboradorDAO.findAll() : new ArrayList<>();
        } catch (Exception e) {
            System.err.println(">>> Erro ao carregar dados do banco: " + e.getMessage());
            agendamentos = new ArrayList<>();
            salas = new ArrayList<>();
            recursosDisponiveis = new ArrayList<>();
            colaboradoresDisponiveis = new ArrayList<>();
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
        idsParticipantesSelecionados = new ArrayList<>();
        alertaEmail = false;
    }

    public void calcularCustoTotal() {
        BigDecimal custoHoraSala = BigDecimal.ZERO;
        BigDecimal custoHoraParticipantes = BigDecimal.ZERO;

        // Custo/Hora da Sala
        if (idSalaSelecionada != null && salaReuniaoDAO != null) {
            SalaReuniao sala = salaReuniaoDAO.find(idSalaSelecionada);
            if (sala != null && sala.getValorHora() != null) {
                custoHoraSala = sala.getValorHora();
            }
        }

        // Custo/Hora dos Participantes Selecionados
        if (idsParticipantesSelecionados != null && !idsParticipantesSelecionados.isEmpty() && colaboradorDAO != null) {
            for (Long idColab : idsParticipantesSelecionados) {
                Colaborador c = colaboradorDAO.find(idColab);
                if (c != null && c.getValorHora() != null) {
                    custoHoraParticipantes = custoHoraParticipantes.add(c.getValorHora());
                }
            }
        }

        // Cálculo Total: (Custo Sala + Custo Participantes) * Duração em Horas
        if (novoAgendamento.getDuracaoHoras() != null) {
            BigDecimal duracao = BigDecimal.valueOf(novoAgendamento.getDuracaoHoras());
            BigDecimal custoHoraTotal = custoHoraSala.add(custoHoraParticipantes);

            BigDecimal total = custoHoraTotal.multiply(duracao);
            novoAgendamento.setCustoTotal(total);

            // Alerta "Poderia ser um e-mail?" se o valor ultrapassar R$ 500,00
            alertaEmail = total.compareTo(new BigDecimal("500.00")) > 0;
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

            // Mapeia a lista de participantes selecionados
            List<Colaborador> participantes = new ArrayList<>();
            if (idsParticipantesSelecionados != null && colaboradorDAO != null) {
                for (Long idColab : idsParticipantesSelecionados) {
                    Colaborador c = colaboradorDAO.find(idColab);
                    if (c != null) {
                        participantes.add(c);
                    }
                }
            }
            novoAgendamento.setParticipantes(participantes);

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

    public List<Colaborador> getColaboradoresDisponiveis() { return colaboradoresDisponiveis; }
    public void setColaboradoresDisponiveis(List<Colaborador> colaboradoresDisponiveis) { this.colaboradoresDisponiveis = colaboradoresDisponiveis; }

    public AgendamentoReuniao getNovoAgendamento() { return novoAgendamento; }
    public void setNovoAgendamento(AgendamentoReuniao novoAgendamento) { this.novoAgendamento = novoAgendamento; }

    public Long getIdSalaSelecionada() { return idSalaSelecionada; }
    public void setIdSalaSelecionada(Long idSalaSelecionada) { this.idSalaSelecionada = idSalaSelecionada; }

    public List<Long> getIdsParticipantesSelecionados() { return idsParticipantesSelecionados; }
    public void setIdsParticipantesSelecionados(List<Long> idsParticipantesSelecionados) { this.idsParticipantesSelecionados = idsParticipantesSelecionados; }

    public boolean isAlertaEmail() { return alertaEmail; }
    public void setAlertaEmail(boolean alertaEmail) { this.alertaEmail = alertaEmail; }

    public StatusAgendamento[] getStatusAgendamentoValues() { return StatusAgendamento.values(); }
    public TipoReuniao[] getTiposReuniao() { return TipoReuniao.values(); }
}