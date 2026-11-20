package br.upf.projectcalccorp.dao;

import br.upf.projectcalccorp.model.AgendamentoReuniao;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

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

    public List<AgendamentoReuniao> buscarPorNome(String nome) {
        String jpql = "SELECT a FROM AgendamentoReuniao a WHERE LOWER(a.nome) LIKE :nome ORDER BY a.id DESC";
        TypedQuery<AgendamentoReuniao> query = em.createQuery(jpql, AgendamentoReuniao.class);
        String termo = (nome != null) ? nome.toLowerCase().trim() : "";
        query.setParameter("nome", "%" + termo + "%");

        List<AgendamentoReuniao> resultados = query.getResultList();
        for (AgendamentoReuniao agendamento : resultados) {
            if (agendamento.getRecursos() != null) {
                agendamento.getRecursos().size();
            }
            if (agendamento.getParticipantes() != null) {
                agendamento.getParticipantes().size();
            }
        }
        return resultados;
    }

}