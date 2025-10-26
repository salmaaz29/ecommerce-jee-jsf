package ma.fstt.ecommerce_jsf.Beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import ma.fstt.ecommerce_jsf.DAO.ProduitDAO;
import ma.fstt.ecommerce_jsf.Entities.Produit;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named("produitBean")
@SessionScoped
@Getter
@Setter
public class ProduitBean implements Serializable {

    private static final Logger logger = Logger.getLogger(ProduitBean.class.getName());
    @Inject
    private ProduitDAO produitDAO;

    private List<Produit> produits;
    private Produit selectedProduit;

    @PostConstruct
    @Transactional
    public void init() {
        logger.info("Initializing ProduitBean at " + new java.util.Date());
        produits = produitDAO.findAll();
        resetSelectedProduit();
    }

    @Transactional
    public void search() {
        produits = produitDAO.findAll();
        logger.info("Loaded all products: " + (produits != null ? produits.size() : 0));
    }

    @Transactional
    public void searchByNom(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            produits = produitDAO.findAll();
        } else {
            produits = produitDAO.searchByNom(keyword);
        }
        logger.info("Search by nom, keyword: " + keyword + ", results: " + (produits != null ? produits.size() : 0));
    }

    @Transactional
    public void saveProduit() {
        try {
            if (selectedProduit == null) {
                logger.warning("selectedProduit is null in saveProduit, initializing new Produit");
                selectedProduit = new Produit();
            }
            if (selectedProduit.getNom() == null || selectedProduit.getNom().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le nom du produit est requis"));
                return;
            }
            if (selectedProduit.getPrix() == null || selectedProduit.getPrix() < 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le prix doit être positif"));
                return;
            }
            if (selectedProduit.getImage() == null || selectedProduit.getImage().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le chemin de l'image est requis"));
                return;
            }
            if (selectedProduit.getStock() < 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le stock doit être positif"));
                return;
            }
            if (selectedProduit.getId_produit() == null) {
                produitDAO.create(selectedProduit);
                produits.add(selectedProduit);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Produit ajouté avec succès"));
                logger.info("Produit ajouté : " + selectedProduit.getNom());
            } else {
                produitDAO.update(selectedProduit);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Produit modifié avec succès"));
                logger.info("Produit modifié : " + selectedProduit.getNom());
            }
            resetSelectedProduit();
            produits = produitDAO.findAll(); // Refresh the list
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de l'enregistrement du produit : " + e.getMessage()));
            logger.severe("Erreur lors de l'enregistrement du produit : " + e.getMessage());
        }
    }

    @Transactional
    public void editProduit(Produit produit) {
        if (produit == null || produit.getId_produit() == null) {
            logger.warning("editProduit received null produit or ID at " + new java.util.Date());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Produit à modifier non trouvé"));
            resetSelectedProduit();
            return;
        }
        selectedProduit = produitDAO.findById(produit.getId_produit());
        if (selectedProduit == null) {
            logger.warning("Produit with ID " + produit.getId_produit() + " not found in database at " + new java.util.Date());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Produit à modifier non trouvé"));
            resetSelectedProduit();
        } else {
            logger.info("Editing produit with ID " + selectedProduit.getId_produit() +
                    ", nom=" + selectedProduit.getNom() +
                    ", prix=" + selectedProduit.getPrix() +
                    ", image=" + selectedProduit.getImage() +
                    ", stock=" + selectedProduit.getStock() +
                    " at " + new java.util.Date());
        }
    }

    @Transactional
    public void deleteProduit(Produit produit) {
        try {
            if (produit == null || produit.getId_produit() == null) {
                logger.warning("deleteProduit received null produit or ID at " + new java.util.Date());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Produit à supprimer non trouvé"));
                return;
            }
            produitDAO.delete(produit.getId_produit());
            produits.remove(produit);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Produit supprimé avec succès"));
            logger.info("Produit supprimé : " + produit.getNom() + " at " + new java.util.Date());
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de la suppression du produit : " + e.getMessage()));
            logger.severe("Erreur lors de la suppression du produit : " + e.getMessage());
        }
    }

    public void resetSelectedProduit() {
        selectedProduit = new Produit();
        logger.info("Reset selectedProduit at " + new java.util.Date());
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    public Produit getSelectedProduit() {
        if (selectedProduit == null) {
            logger.warning("getSelectedProduit returned null, initializing new Produit at " + new java.util.Date());
            selectedProduit = new Produit();
        }
        return selectedProduit;
    }

    public void setSelectedProduit(Produit selectedProduit) {
        this.selectedProduit = selectedProduit;
    }
}