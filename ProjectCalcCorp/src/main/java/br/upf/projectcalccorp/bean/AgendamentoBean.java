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
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("agendamentoBean")
@ViewScoped
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
    private List<Long> idsRecursosSelecionados;
    private boolean alertaEmail;
    private String filtroNome;

    public AgendamentoBean() {
    }

    @PostConstruct
    public void init() {
        carregarDados();
        prepararNovoAgendamento();
    }

    public void carregarDados() {
        try {
            filtroNome = null;
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

    public void pesquisar() {
        try {
            if (filtroNome == null || filtroNome.trim().isEmpty()) {
                agendamentos = (agendamentoReuniaoDAO != null) ? agendamentoReuniaoDAO.findAll() : new ArrayList<>();
            } else {
                agendamentos = (agendamentoReuniaoDAO != null) ? agendamentoReuniaoDAO.buscarPorNome(filtroNome) : new ArrayList<>();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao pesquisar agendamentos: " + e.getMessage()));
        }
    }

    public void limparPesquisa() {
        this.filtroNome = null;
        this.agendamentos = (agendamentoReuniaoDAO != null) ? agendamentoReuniaoDAO.findAll() : new ArrayList<>();
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
        idsRecursosSelecionados = new ArrayList<>();
        alertaEmail = false;
    }

    public void prepararEdicao(AgendamentoReuniao agendamento) {
        AgendamentoReuniao gerenciado = (agendamentoReuniaoDAO != null && agendamento.getId() != null)
                ? agendamentoReuniaoDAO.find(agendamento.getId())
                : null;
        this.novoAgendamento = (gerenciado != null) ? gerenciado : agendamento;

        this.idSalaSelecionada = (this.novoAgendamento.getSala() != null)
                ? this.novoAgendamento.getSala().getId()
                : null;

        this.idsParticipantesSelecionados = new ArrayList<>();
        try {
            if (this.novoAgendamento.getParticipantes() != null) {
                for (Colaborador c : this.novoAgendamento.getParticipantes()) {
                    if (c != null && c.getId() != null) {
                        this.idsParticipantesSelecionados.add(c.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(">>> Aviso ao carregar participantes para edição: " + e.getMessage());
        }

        this.idsRecursosSelecionados = new ArrayList<>();
        try {
            if (this.novoAgendamento.getRecursos() != null) {
                for (RecursoTecnologico r : this.novoAgendamento.getRecursos()) {
                    if (r != null && r.getId() != null) {
                        this.idsRecursosSelecionados.add(r.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(">>> Aviso ao carregar recursos para edição: " + e.getMessage());
        }

        calcularCustoTotal();
    }

    public void cancelar(AgendamentoReuniao agendamento) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            AgendamentoReuniao alvo = (agendamentoReuniaoDAO != null && agendamento.getId() != null)
                    ? agendamentoReuniaoDAO.find(agendamento.getId())
                    : agendamento;

            if (alvo.getStatus() == StatusAgendamento.CANCELADO) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Este agendamento já está cancelado."));
                return;
            }

            alvo.setStatus(StatusAgendamento.CANCELADO);
            agendamentoReuniaoDAO.edit(alvo);
            carregarDados();

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cancelado", "Agendamento cancelado com sucesso!"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao cancelar agendamento: " + e.getMessage()));
        }
    }

    public void calcularCustoTotal() {
        BigDecimal custoHoraSala = BigDecimal.ZERO;
        BigDecimal custoHoraParticipantes = BigDecimal.ZERO;

        if (idSalaSelecionada != null && salaReuniaoDAO != null) {
            SalaReuniao sala = salaReuniaoDAO.find(idSalaSelecionada);
            if (sala != null && sala.getValorHora() != null) {
                custoHoraSala = sala.getValorHora();
            }
        }

        if (idsParticipantesSelecionados != null && !idsParticipantesSelecionados.isEmpty() && colaboradorDAO != null) {
            for (Long idColab : idsParticipantesSelecionados) {
                Colaborador c = colaboradorDAO.find(idColab);
                if (c != null && c.getValorHora() != null) {
                    custoHoraParticipantes = custoHoraParticipantes.add(c.getValorHora());
                }
            }
        }

        if (novoAgendamento != null && novoAgendamento.getDuracaoHoras() != null) {
            BigDecimal duracao = BigDecimal.valueOf(novoAgendamento.getDuracaoHoras());
            BigDecimal custoHoraTotal = custoHoraSala.add(custoHoraParticipantes);

            BigDecimal total = custoHoraTotal.multiply(duracao);
            novoAgendamento.setCustoTotal(total);

            alertaEmail = total.compareTo(new BigDecimal("500.00")) > 0;
        }
    }

    public void salvar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            Colaborador usuarioLogado = (session != null) ? (Colaborador) session.getAttribute("usuarioLogado") : null;

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

            if (novoAgendamento.getSolicitante() == null) {
                novoAgendamento.setSolicitante(usuarioLogado);
            }

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

            List<RecursoTecnologico> recursos = new ArrayList<>();
            if (idsRecursosSelecionados != null && recursoTecnologicoDAO != null) {
                for (Long idRec : idsRecursosSelecionados) {
                    RecursoTecnologico r = recursoTecnologicoDAO.find(idRec);
                    if (r != null) {
                        recursos.add(r);
                    }
                }
            }
            novoAgendamento.setRecursos(recursos);

            long inicioMs = novoAgendamento.getDataHoraInicio().getTime();
            long duracaoMs = (long) (novoAgendamento.getDuracaoHoras() * 3600000);
            novoAgendamento.setDataHoraFim(new Date(inicioMs + duracaoMs));

            calcularCustoTotal();

            if (novoAgendamento.getId() == null) {
                if (novoAgendamento.getStatus() == null) {
                    novoAgendamento.setStatus(StatusAgendamento.AGENDADO);
                }
                agendamentoReuniaoDAO.create(novoAgendamento);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Agendamento criado com sucesso!"));
            } else {
                agendamentoReuniaoDAO.edit(novoAgendamento);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Agendamento atualizado com sucesso!"));
            }

            carregarDados();
            prepararNovoAgendamento();
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

    public List<Long> getIdsRecursosSelecionados() { return idsRecursosSelecionados; }
    public void setIdsRecursosSelecionados(List<Long> idsRecursosSelecionados) { this.idsRecursosSelecionados = idsRecursosSelecionados; }

    public boolean isAlertaEmail() { return alertaEmail; }
    public void setAlertaEmail(boolean alertaEmail) { this.alertaEmail = alertaEmail; }

    public String getFiltroNome() { return filtroNome; }
    public void setFiltroNome(String filtroNome) { this.filtroNome = filtroNome; }

    public StatusAgendamento[] getStatusAgendamentoValues() { return StatusAgendamento.values(); }
    public TipoReuniao[] getTiposReuniao() { return TipoReuniao.values(); }
}