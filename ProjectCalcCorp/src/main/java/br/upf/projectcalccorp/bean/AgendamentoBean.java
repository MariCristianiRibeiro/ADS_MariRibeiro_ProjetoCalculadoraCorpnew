package br.upf.projectcalccorp.bean;

import br.upf.projectcalccorp.dao.AgendamentoReuniaoDAO;
import br.upf.projectcalccorp.model.AgendamentoReuniao;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("agendamentoBean")
@RequestScoped
public class AgendamentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AgendamentoReuniaoDAO agendamentoReuniaoDAO;

    private List<AgendamentoReuniao> agendamentos;

    public AgendamentoBean() {
    }

    @PostConstruct
    public void init() {
        try {
            if (agendamentoReuniaoDAO != null) {
                agendamentos = agendamentoReuniaoDAO.findAll();
            } else {
                agendamentos = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println(">>> AVISO: Erro ao carregar agendamentos da BD (modo de segurança ativado): " + e.getMessage());
            // Inicializa a lista vazia para evitar que o erro 500 derrube o ecrã
            agendamentos = new ArrayList<>();
        }
    }

    public List<AgendamentoReuniao> getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(List<AgendamentoReuniao> agendamentos) {
        this.agendamentos = agendamentos;
    }
}