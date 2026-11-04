package br.upf.projectcalccorp.dao;

import br.upf.projectcalccorp.model.SalaReuniao;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class SalaReuniaoDAO extends GenericDAO<SalaReuniao> {

    @PersistenceContext(unitName = "ProjectCalcCorp")
    private EntityManager em;

    public SalaReuniaoDAO() {
        super(SalaReuniao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    // ADICIONADO: Método de busca por nome exigido pelo SalaReuniaoBean
    public List<SalaReuniao> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return findAll();
        }
        return em.createQuery(
            "SELECT s FROM SalaReuniao s WHERE LOWER(s.nome) LIKE LOWER(:nome)", SalaReuniao.class)
            .setParameter("nome", "%" + nome.trim() + "%")
            .getResultList();
    }
}