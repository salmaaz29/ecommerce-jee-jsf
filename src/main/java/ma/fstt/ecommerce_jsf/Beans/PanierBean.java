package ma.fstt.ecommerce_jsf.Beans;


import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import ma.fstt.ecommerce_jsf.Entities.LignePanier;
import ma.fstt.ecommerce_jsf.Entities.Panier;
import ma.fstt.ecommerce_jsf.Entities.Produit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Locale.filter;

@Named
@SessionScoped
@Getter
@Setter
public class PanierBean implements Serializable {

    @Inject
    UserBean userBean ;

    private Panier panier = new Panier();
    private List<LignePanier> lignes = new ArrayList<>();


    private void updatePanier() {
        panier.setLignes(lignes);
        panier.setUser(userBean.getCurrentuser());
    }

    // modifier quantite de panier

    public void updateQuantity(LignePanier ligne, int quantite) {
        if (quantite <= 0) {
            lignes.remove(ligne);
        } else {
            ligne.setQuantite(quantite);
        }
        updatePanier();
    }

    // supprimer un ligne de panier

    public void remove(LignePanier ligne) {
        lignes.remove(ligne);
        updatePanier();
    }


        // recuperer les total du panier

    public double getTotal() {
        return lignes.stream()
                .mapToDouble(l -> l.getProduit().getPrix() * l.getQuantite())
                .sum();
    }


    // ajouter au panier

    public void addToCart(Produit produit) {
        LignePanier ligne = lignes.stream()
                .filter(l -> l.getProduit().getId_produit().equals(produit.getId_produit()))
                .findFirst().orElse(null);

        if (ligne == null) {
            ligne = new LignePanier();
            ligne.setProduit(produit);
            ligne.setQuantite(1);
            lignes.add(ligne);
        } else {
            ligne.setQuantite(ligne.getQuantite() + 1);
        }
        updatePanier() ;
    }

}
