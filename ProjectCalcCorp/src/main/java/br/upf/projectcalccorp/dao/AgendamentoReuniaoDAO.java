/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.upf.projectcalccorp.dao;

import br.upf.projectcalccorp.model.AgendamentoReuniao;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class AgendamentoReuniaoDAO extends GenericDAO<AgendamentoReuniao> {

    @PersistenceContext(unitName = "GestaoReunioesPU")
    private EntityManager em;

    public AgendamentoReuniaoDAO() {
        super(AgendamentoReuniao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
