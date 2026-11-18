package br.upf.projectcalccorp.dao;

import br.upf.projectcalccorp.model.AgendamentoReuniao;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class AgendamentoReuniaoDAO extends GenericDAO<AgendamentoReuniao> {

    @PersistenceContext(unitName = "ProjectCalcCorp")
    private EntityManager em;

    public AgendamentoReuniaoDAO() {
        super(AgendamentoReuniao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AgendamentoReuniao find(Object id) {
        AgendamentoReuniao agendamento = super.find(id);
        if (agendamento != null) {
            if (agendamento.getRecursos() != null) {
                agendamento.getRecursos().size();
            }
            if (agendamento.getParticipantes() != null) {
                agendamento.getParticipantes().size();
            }
        }
        return agendamento;
    }
}