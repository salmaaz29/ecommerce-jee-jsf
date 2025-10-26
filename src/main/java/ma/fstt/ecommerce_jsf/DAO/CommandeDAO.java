package ma.fstt.ecommerce_jsf.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import ma.fstt.ecommerce_jsf.Beans.CommandeBean;
import ma.fstt.ecommerce_jsf.Entities.Commande;
import ma.fstt.ecommerce_jsf.Entities.User;

import java.util.List;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class CommandeDAO extends GenericDAO<Commande> {

    private static final Logger logger = Logger.getLogger(CommandeDAO.class.getName());

    public CommandeDAO() {
        super(Commande.class);
    }

    @Transactional
    public List<Commande> findByUser(User user) {
        return em.createQuery(
                        "SELECT c FROM Commande c WHERE c.user = :user", Commande.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Transactional
    public List<Commande> findByUserId(Long userId) {
        return em.createQuery(
                        "SELECT c FROM Commande c WHERE c.user.id_user = :userId", Commande.class)
                .setParameter("userId", userId)
                .getResultList();
    }
    public void flush() {
        em.flush();
    }

    @Transactional
    public int updateStatut(Long commandeId, String nouveauStatut) {
        try {
            logger.info("🔧 DAO - Mise à jour commande " + commandeId + " avec statut: " + nouveauStatut);

            // Méthode native SQL pour être sûr
            Query query = em.createNativeQuery(
                    "UPDATE commande SET statut = ? WHERE id_commande = ?"
            );
            query.setParameter(1, nouveauStatut);
            query.setParameter(2, commandeId);

            int result = query.executeUpdate();
            em.flush();

            logger.info("✅ DAO - " + result + " ligne(s) mise(s) à jour");
            return result;

        } catch (Exception e) {
            logger.severe("❌ DAO - Erreur: " + e.getMessage());
            throw new RuntimeException("Erreur mise à jour statut", e);
        }
    }
}