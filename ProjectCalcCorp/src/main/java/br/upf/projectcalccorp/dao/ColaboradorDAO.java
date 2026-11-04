package br.upf.projectcalccorp.dao;

import br.upf.projectcalccorp.model.Colaborador;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ColaboradorDAO extends GenericDAO<Colaborador> {

    @PersistenceContext(unitName = "ProjectCalcCorp")
    private EntityManager em;

    public ColaboradorDAO() {
        super(Colaborador.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Valida o acesso comparando a matrícula ou o e-mail com a senha no PostgreSQL.
     */
    public Colaborador autenticar(String login, String senha) {
        if (login == null || senha == null || login.trim().isEmpty() || senha.trim().isEmpty()) {
            return null;
        }
        try {
            return em.createQuery(
                "SELECT c FROM Colaborador c WHERE (LOWER(c.email) = LOWER(:login) OR LOWER(c.matricula) = LOWER(:login)) AND c.senha = :senha", 
                Colaborador.class)
                .setParameter("login", login.trim())
                .setParameter("senha", senha.trim())
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Colaborador> buscarPorMatriculaOuEmail(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return findAll();
        }
        String busca = "%" + termo.trim().toLowerCase() + "%";
        return em.createQuery(
            "SELECT c FROM Colaborador c WHERE LOWER(c.nome) LIKE :busca OR LOWER(c.matricula) LIKE :busca OR LOWER(c.email) LIKE :busca", 
            Colaborador.class)
            .setParameter("busca", busca)
            .getResultList();
    }

    public List<Colaborador> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return findAll();
        }
        return em.createQuery(
            "SELECT c FROM Colaborador c WHERE LOWER(c.nome) LIKE LOWER(:nome)", 
            Colaborador.class)
            .setParameter("nome", "%" + nome.trim() + "%")
            .getResultList();
    }
}