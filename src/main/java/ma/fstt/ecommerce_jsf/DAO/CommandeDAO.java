package ma.fstt.ecommerce_jsf.DAO;

import jakarta.persistence.EntityManager;
import ma.fstt.ecommerce_jsf.Entities.Commande;
import ma.fstt.ecommerce_jsf.Entities.User;

import java.util.List;

public class CommandeDAO extends GenericDAO<Commande> {

    public CommandeDAO() {
        super(Commande.class);
    }
    public List<Commande> findByUser(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Commande c WHERE c.user = :user", Commande.class)
                    .setParameter("user", user)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Commande> findByUserId(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Commande c WHERE c.user.id = :userId", Commande.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

