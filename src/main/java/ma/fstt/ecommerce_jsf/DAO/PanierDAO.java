package ma.fstt.ecommerce_jsf.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import ma.fstt.ecommerce_jsf.Entities.Panier;

@Named
@ApplicationScoped
public class PanierDAO extends GenericDAO<Panier> {

    public PanierDAO() {
        super(Panier.class);
    }

    // ✅ AVEC @Transactional
    @Transactional
    public Panier findByUser(Long userId) {
        try {
            return em.createQuery(
                            "SELECT p FROM Panier p WHERE p.user.id_user = :userId ORDER BY p.datecreation DESC",
                            Panier.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}