package br.upf.projectcalccorp.dao;

import br.upf.projectcalccorp.model.RecursoTecnologico;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class RecursoTecnologicoDAO extends GenericDAO<RecursoTecnologico> {

    @PersistenceContext(unitName = "ProjectCalcCorp")
    private EntityManager em;

    public RecursoTecnologicoDAO() {
        super(RecursoTecnologico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    // ADICIONADO: Método de busca por nome exigido pelo RecursoTecnologicoBean
    public List<RecursoTecnologico> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return findAll();
        }
        return em.createQuery(
            "SELECT r FROM RecursoTecnologico r WHERE LOWER(r.nome) LIKE LOWER(:nome)", RecursoTecnologico.class)
            .setParameter("nome", "%" + nome.trim() + "%")
            .getResultList();
    }
}