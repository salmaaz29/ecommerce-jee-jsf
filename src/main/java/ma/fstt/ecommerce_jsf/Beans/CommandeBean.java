package ma.fstt.ecommerce_jsf.Beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import ma.fstt.ecommerce_jsf.DAO.CommandeDAO;
import ma.fstt.ecommerce_jsf.DAO.LigneCommandeDAO;
import ma.fstt.ecommerce_jsf.DAO.LignePanierDAO;
import ma.fstt.ecommerce_jsf.DAO.PanierDAO;
import ma.fstt.ecommerce_jsf.Entities.Commande;
import ma.fstt.ecommerce_jsf.Entities.LigneCommande;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Named("commandeBean")
@SessionScoped
@Getter
@Setter
public class CommandeBean implements Serializable {

    private static final Logger logger = Logger.getLogger(CommandeBean.class.getName());


    @Inject
    private PanierBean panierBean;

    @Inject
    private UserBean userBean;

    @Inject
    private CommandeDAO commandeDAO;

    @Inject
    private LigneCommandeDAO ligneCommandeDAO;
    @Inject
    private LignePanierDAO lignePanierDAO;
    @Inject
    private PanierDAO panierDAO;


    private List<Commande> commandes;

    @Transactional
    public String validerCommande() {
        if (panierBean.getLignes().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Panier vide", "Votre panier est vide"));
            return null;
        }

        try {
            // Créer la commande
            Commande commande = new Commande();
            commande.setUser(userBean.getCurrentuser());
            commande.setDateCommande(new Date());
            commande.setStatut("EN_COURS");
            commande.setTotal(panierBean.getTotal());
            commandeDAO.create(commande);

            // Créer les lignes de commande
            for (var lp : panierBean.getLignes()) {
                LigneCommande lc = new LigneCommande();
                lc.setCommande(commande);
                lc.setProduit(lp.getProduit());
                lc.setQuantite(lp.getQuantite());
                ligneCommandeDAO.create(lc);
            }

            // ✅ CORRECTION : Vider complètement le panier (mémoire + base)
            viderPanierComplet();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Commande validée", "Votre commande a été enregistrée avec succès"));

            return "confirmation.xhtml?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de la validation de la commande: " + e.getMessage()));
            return null;
        }
    }

    // ✅ AJOUTEZ cette méthode pour vider complètement le panier
    @Transactional
    private void viderPanierComplet() {
        try {
            // 1. Vider les lignes de panier en base de données
            if (panierBean.getPanier() != null && panierBean.getPanier().getId_panier() != null) {
                lignePanierDAO.deleteByPanierId(panierBean.getPanier().getId_panier());
            }

            // 2. Vider la liste en mémoire
            panierBean.getLignes().clear();

            // 3. Mettre à jour le total à 0
            if (panierBean.getPanier() != null) {
                panierBean.getPanier().setTotal(0.0);
                panierDAO.update(panierBean.getPanier());
            }

            logger.info("✅ Panier vidé complètement (mémoire + base)");

        } catch (Exception e) {
            logger.severe("❌ Erreur lors du vidage du panier: " + e.getMessage());
            throw new RuntimeException("Erreur vidage panier", e);
        }
    }

    // ✅ Méthode pour charger les commandes de l'utilisateur courant
    @Transactional
    public List<Commande> loadCommandesUtilisateur() {
        if (userBean.getCurrentuser() == null) {
            return new ArrayList<>();
        }
        return commandeDAO.findByUserId(userBean.getCurrentuser().getId_user());
    }
}