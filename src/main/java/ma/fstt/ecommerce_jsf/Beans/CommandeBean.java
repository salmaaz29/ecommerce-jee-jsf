package ma.fstt.ecommerce_jsf.Beans;


import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import ma.fstt.ecommerce_jsf.DAO.CommandeDAO;
import ma.fstt.ecommerce_jsf.DAO.LigneCommandeDAO;
import ma.fstt.ecommerce_jsf.Entities.Commande;
import ma.fstt.ecommerce_jsf.Entities.LigneCommande;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
@Getter
@Setter
public class CommandeBean  implements Serializable {

    @Inject
    private PanierBean panierBean;

    @Inject
    private UserBean userBean;

    @Inject
    private CommandeDAO commandeDAO;

    @Inject
    private LigneCommandeDAO ligneCommandeDAO;

    private List<Commande> commandes;


    public String validerCommande() {
        if (panierBean.getLignes().isEmpty()) {
            return null;
        }
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

        // Vider le panier
        panierBean.getLignes().clear();

        return "/confirmation.xhtml?faces-redirect=true";
    }

    public List<Commande> getCommandesUtilisateur() {
        if (userBean.getCurrentuser() == null) return List.of();
        return commandeDAO.findAll().stream()
                .filter(c -> c.getUser().getId_user().equals(userBean.getCurrentuser().getId_user()))
                .toList();
    }
}
