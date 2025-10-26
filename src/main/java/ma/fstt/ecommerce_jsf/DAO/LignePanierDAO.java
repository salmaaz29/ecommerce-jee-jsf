package ma.fstt.ecommerce_jsf.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import ma.fstt.ecommerce_jsf.Beans.CommandeBean;
import ma.fstt.ecommerce_jsf.Entities.LignePanier;
import ma.fstt.ecommerce_jsf.Entities.Panier;

import java.util.List;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class LignePanierDAO extends GenericDAO<LignePanier> {

    private static final Logger logger = Logger.getLogger(LignePanierDAO.class.getName());


    public LignePanierDAO() {
        super(LignePanier.class);
    }

    // 1. Trouver toutes les lignes d'un panier
    @Transactional
    public List<LignePanier> findByPanier(Panier panier) {
        return em.createQuery(
                        "SELECT lp FROM LignePanier lp WHERE lp.panier = :panier", LignePanier.class)
                .setParameter("panier", panier)
                .getResultList();
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
    // 3. Trouver une ligne spécifique d'un produit dans un panier
    @Transactional
    public LignePanier findByPanierAndProduit(Long panierId, Long produitId) {
        try {
            return em.createQuery(
                            "SELECT lp FROM LignePanier lp WHERE lp.panier.id_panier = :panierId AND lp.produit.id_produit = :produitId", LignePanier.class)
                    .setParameter("panierId", panierId)
                    .setParameter("produitId", produitId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // 4. Mettre à jour la quantité d'une ligne de panier
    @Transactional
    public void updateQuantite(Long lignePanierId, int nouvelleQuantite) {
        LignePanier ligne = em.find(LignePanier.class, lignePanierId);
        if (ligne != null) {
            ligne.setQuantite(nouvelleQuantite);
            em.merge(ligne);
        }
    }

    // 5. Calculer le sous-total d'une ligne (quantité * prix)
    @Transactional
    public Double calculerSousTotal(Long lignePanierId) {
        return em.createQuery(
                        "SELECT lp.quantite * lp.produit.prix FROM LignePanier lp WHERE lp.id_ligne = :id", Double.class)
                .setParameter("id", lignePanierId)
                .getSingleResult();
    }

    // 6. Supprimer toutes les lignes d'un panier
    @Transactional
    public void deleteByPanier(Panier panier) {
        em.createQuery("DELETE FROM LignePanier lp WHERE lp.panier = :panier")
                .setParameter("panier", panier)
                .executeUpdate();
    }

    // 7. Vérifier si un produit est déjà dans le panier
    @Transactional
    public boolean existsInPanier(Long panierId, Long produitId) {
        Long count = em.createQuery(
                        "SELECT COUNT(lp) FROM LignePanier lp WHERE lp.panier.id_panier = :panierId AND lp.produit.id_produit = :produitId", Long.class)
                .setParameter("panierId", panierId)
                .setParameter("produitId", produitId)
                .getSingleResult();
        return count > 0;
    }
}