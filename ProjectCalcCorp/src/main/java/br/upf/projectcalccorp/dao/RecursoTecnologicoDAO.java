/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.upf.projectcalccorp.dao;

import br.upf.projectcalccorp.model.RecursoTecnologico;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class RecursoTecnologicoDAO extends GenericDAO<RecursoTecnologico> {

    @PersistenceContext(unitName = "GestaoReunioesPU")
    private EntityManager em;

    public RecursoTecnologicoDAO() {
        super(RecursoTecnologico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<RecursoTecnologico> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return findAll();
        }
        return em.createQuery("SELECT r FROM RecursoTecnologico r WHERE LOWER(r.nome) LIKE :nome", RecursoTecnologico.class)
                 .setParameter("nome", "%" + nome.toLowerCase() + "%")
                 .getResultList();
    }
}
