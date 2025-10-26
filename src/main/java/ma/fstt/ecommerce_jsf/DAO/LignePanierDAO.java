package ma.fstt.ecommerce_jsf.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import ma.fstt.ecommerce_jsf.Entities.LignePanier;
import java.util.List;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class LignePanierDAO extends GenericDAO<LignePanier> {

    private static final Logger logger = Logger.getLogger(LignePanierDAO.class.getName());


    public LignePanierDAO() {
        super(LignePanier.class);
    }


    @Transactional
    public void deleteByPanierId(Long panierId) {
        try {
            Query query = em.createQuery(
                    "DELETE FROM LignePanier lp WHERE lp.panier.id_panier = :panierId"
            );
            query.setParameter("panierId", panierId);
            int deletedCount = query.executeUpdate();
            em.flush();

            logger.info("✅ " + deletedCount + " lignes de panier supprimées pour le panier ID: " + panierId);

        } catch (Exception e) {
            logger.severe("❌ Erreur suppression lignes panier: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    // trouver lesligne de panier par id
    @Transactional
    public List<LignePanier> findByPanierId(Long panierId) {
        return em.createQuery(
                        "SELECT lp FROM LignePanier lp " +
                                "LEFT JOIN FETCH lp.produit " +  // ← Important !
                                "WHERE lp.panier.id_panier = :panierId",
                        LignePanier.class)
                .setParameter("panierId", panierId)
                .getResultList();
    }

}