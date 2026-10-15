/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.upf.projectcalccorp.dao;

import br.upf.projectcalccorp.model.Colaborador;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ColaboradorDAO extends GenericDAO<Colaborador> {

    @PersistenceContext(unitName = "GestaoReunioesPU")
    private EntityManager em;

    public ColaboradorDAO() {
        super(Colaborador.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Colaborador> buscarPorMatriculaOuEmail(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return findAll();
        }
        return em.createQuery("SELECT c FROM Colaborador c WHERE LOWER(c.matricula) LIKE :termo OR LOWER(c.email) LIKE :termo", Colaborador.class)
                 .setParameter("termo", "%" + termo.toLowerCase() + "%")
                 .getResultList();
    }
}
