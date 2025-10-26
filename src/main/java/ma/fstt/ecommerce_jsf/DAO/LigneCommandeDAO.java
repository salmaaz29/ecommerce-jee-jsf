package ma.fstt.ecommerce_jsf.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import ma.fstt.ecommerce_jsf.Entities.Commande;
import ma.fstt.ecommerce_jsf.Entities.LigneCommande;

import java.util.List;

@Named
@ApplicationScoped
public class LigneCommandeDAO extends GenericDAO<LigneCommande> {

    public LigneCommandeDAO() {
        super(LigneCommande.class);
    }

    // 1. Trouver toutes les lignes d'une commande
    @Transactional
    public List<LigneCommande> findByCommande(Commande commande) {
        return em.createQuery(
                        "SELECT lc FROM LigneCommande lc WHERE lc.commande = :commande", LigneCommande.class)
                .setParameter("commande", commande)
                .getResultList();
    }

    // 2. Trouver toutes les lignes d'une commande par ID
    @Transactional
    public List<LigneCommande> findByCommandeId(Long commandeId) {
        return em.createQuery(
                        "SELECT lc FROM LigneCommande lc WHERE lc.commande.id_commande = :commandeId", LigneCommande.class)
                .setParameter("commandeId", commandeId)
                .getResultList();
    }

    // 3. Calculer le total d'une ligne de commande
    @Transactional
    public Double calculerTotalLigne(Long ligneCommandeId) {
        try {
            return em.createQuery(
                            "SELECT lc.quantite * lc.produit.prix FROM LigneCommande lc WHERE lc.id = :id", Double.class)
                    .setParameter("id", ligneCommandeId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0.0;
        }
    }

    // 4. Calculer le total de toute la commande
    @Transactional
    public Double calculerTotalCommande(Long commandeId) {
        try {
            return em.createQuery(
                            "SELECT SUM(lc.quantite * lc.produit.prix) FROM LigneCommande lc WHERE lc.commande.id_commande = :commandeId", Double.class)
                    .setParameter("commandeId", commandeId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0.0;
        }
    }

    // 5. Supprimer toutes les lignes d'une commande
    @Transactional
    public void deleteByCommande(Commande commande) {
        em.createQuery("DELETE FROM LigneCommande lc WHERE lc.commande = :commande")
                .setParameter("commande", commande)
                .executeUpdate();
    }

    // 6. Mettre à jour la quantité d'une ligne
    @Transactional
    public void updateQuantite(Long ligneCommandeId, int nouvelleQuantite) {
        LigneCommande ligne = em.find(LigneCommande.class, ligneCommandeId);
        if (ligne != null) {
            ligne.setQuantite(nouvelleQuantite);
            em.merge(ligne);
        }
    }

    // 7. Trouver les produits les plus commandés
    @Transactional
    public List<Object[]> findProduitsPlusVendus(int limit) {
        return em.createQuery(
                        "SELECT lc.produit, SUM(lc.quantite) as totalVendu " +
                                "FROM LigneCommande lc " +
                                "GROUP BY lc.produit " +
                                "ORDER BY totalVendu DESC", Object[].class)
                .setMaxResults(limit)
                .getResultList();
    }
}