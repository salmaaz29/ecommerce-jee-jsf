package ma.fstt.ecommerce_jsf.DAO;

import jakarta.persistence.EntityManager;
import ma.fstt.ecommerce_jsf.Entities.Commande;
import ma.fstt.ecommerce_jsf.Entities.LigneCommande;

import java.util.List;

public class LigneCommandeDAO extends GenericDAO<LigneCommande> {

    public LigneCommandeDAO() {
        super(LigneCommande.class);
    }

    // 1. Trouver toutes les lignes d'une commande
    public List<LigneCommande> findByCommande(Commande commande) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT lc FROM LigneCommande lc WHERE lc.commande = :commande", LigneCommande.class)
                    .setParameter("commande", commande)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 2. Trouver toutes les lignes d'une commande par ID
    public List<LigneCommande> findByCommandeId(Long commandeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT lc FROM LigneCommande lc WHERE lc.commande.id = :commandeId", LigneCommande.class)
                    .setParameter("commandeId", commandeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 3. Calculer le total d'une ligne de commande
    public Double calculerTotalLigne(Long ligneCommandeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT lc.quantite * lc.produit.prix FROM LigneCommande lc WHERE lc.id = :id", Double.class)
                    .setParameter("id", ligneCommandeId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    // 4. Calculer le total de toute la commande
    public Double calculerTotalCommande(Long commandeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT SUM(lc.quantite * lc.produit.prix) FROM LigneCommande lc WHERE lc.commande.id = :commandeId", Double.class)
                    .setParameter("commandeId", commandeId)
                    .getSingleResult();
        } catch (Exception e) {
            return 0.0; // Si pas de lignes
        } finally {
            em.close();
        }
    }

    // 5. Supprimer toutes les lignes d'une commande
    public void deleteByCommande(Commande commande) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM LigneCommande lc WHERE lc.commande = :commande")
                    .setParameter("commande", commande)
                    .executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // 6. Mettre à jour la quantité d'une ligne
    public void updateQuantite(Long ligneCommandeId, int nouvelleQuantite) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            LigneCommande ligne = em.find(LigneCommande.class, ligneCommandeId);
            if (ligne != null) {
                ligne.setQuantite(nouvelleQuantite);
                em.merge(ligne);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // 7. Trouver les produits les plus commandés
    public List<Object[]> findProduitsPlusVendus(int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT lc.produit, SUM(lc.quantite) as totalVendu " +
                                    "FROM LigneCommande lc " +
                                    "GROUP BY lc.produit " +
                                    "ORDER BY totalVendu DESC", Object[].class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}