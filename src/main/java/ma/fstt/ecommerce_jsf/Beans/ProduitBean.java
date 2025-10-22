package ma.fstt.ecommerce_jsf.Beans;


import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import ma.fstt.ecommerce_jsf.DAO.ProduitDAO;
import ma.fstt.ecommerce_jsf.Entities.Produit;

import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
@Getter
@Setter
public class ProduitBean implements Serializable {

    @Inject
    private ProduitDAO produitDAO;

    private List<Produit> produits ;

    @PostConstruct
    public void init(){
        produits = produitDAO.findAll();
    }
    public void search() {
        produits = produitDAO.findAll(); // temporaire
    }
}
