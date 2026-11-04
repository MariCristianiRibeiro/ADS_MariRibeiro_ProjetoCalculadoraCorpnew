package br.upf.projectcalccorp.dao;

import br.upf.projectcalccorp.model.AgendamentoReuniao;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class AgendamentoReuniaoDAO extends GenericDAO<AgendamentoReuniao> {

    // CORREÇÃO: unitName aponta exatamente para "ProjectCalcCorp" do persistence.xml
    @PersistenceContext(unitName = "ProjectCalcCorp")
    private EntityManager em;

    public AgendamentoReuniaoDAO() {
        super(AgendamentoReuniao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}