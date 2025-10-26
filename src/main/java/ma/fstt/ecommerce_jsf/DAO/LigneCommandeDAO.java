package ma.fstt.ecommerce_jsf.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
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

}