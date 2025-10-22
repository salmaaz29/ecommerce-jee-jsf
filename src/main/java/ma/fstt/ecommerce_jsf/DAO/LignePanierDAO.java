package ma.fstt.ecommerce_jsf.DAO;

import jakarta.persistence.EntityManager;
import ma.fstt.ecommerce_jsf.Entities.LignePanier;
import ma.fstt.ecommerce_jsf.Entities.Panier;

import java.util.List;

public class LignePanierDAO extends GenericDAO<LignePanier> {

    public LignePanierDAO() {
        super(LignePanier.class);
    }

    // 1. Trouver toutes les lignes d'un panier
    public List<LignePanier> findByPanier(Panier panier) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT lp FROM LignePanier lp WHERE lp.panier = :panier", LignePanier.class)
                    .setParameter("panier", panier)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 2. Trouver toutes les lignes d'un panier par ID
    public List<LignePanier> findByPanierId(Long panierId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT lp FROM LignePanier lp WHERE lp.panier.id_panier = :panierId", LignePanier.class)
                    .setParameter("panierId", panierId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 3. Trouver une ligne spécifique d'un produit dans un panier
    public LignePanier findByPanierAndProduit(Long panierId, Long produitId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT lp FROM LignePanier lp WHERE lp.panier.id_panier = :panierId AND lp.produit.id = :produitId", LignePanier.class)
                    .setParameter("panierId", panierId)
                    .setParameter("produitId", produitId)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Aucune ligne trouvée
        } finally {
            em.close();
        }
    }

    // 4. Mettre à jour la quantité d'une ligne de panier
    public void updateQuantite(Long lignePanierId, int nouvelleQuantite) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            LignePanier ligne = em.find(LignePanier.class, lignePanierId);
            if (ligne != null) {
                ligne.setQuantite(nouvelleQuantite);
                em.merge(ligne);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // 5. Calculer le sous-total d'une ligne (quantité * prix)
    public Double calculerSousTotal(Long lignePanierId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT lp.quantite * lp.produit.prix FROM LignePanier lp WHERE lp.id_ligne = :id", Double.class)
                    .setParameter("id", lignePanierId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    // 6. Supprimer toutes les lignes d'un panier
    public void deleteByPanier(Panier panier) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM LignePanier lp WHERE lp.panier = :panier")
                    .setParameter("panier", panier)
                    .executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // 7. Vérifier si un produit est déjà dans le panier
    public boolean existsInPanier(Long panierId, Long produitId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(lp) FROM LignePanier lp WHERE lp.panier.id_panier = :panierId AND lp.produit.id = :produitId", Long.class)
                    .setParameter("panierId", panierId)
                    .setParameter("produitId", produitId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
