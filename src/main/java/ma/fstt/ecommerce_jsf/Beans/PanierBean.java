package ma.fstt.ecommerce_jsf.Beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import ma.fstt.ecommerce_jsf.DAO.LignePanierDAO;
import ma.fstt.ecommerce_jsf.DAO.PanierDAO;
import ma.fstt.ecommerce_jsf.DAO.ProduitDAO;
import ma.fstt.ecommerce_jsf.Entities.LignePanier;
import ma.fstt.ecommerce_jsf.Entities.Panier;
import ma.fstt.ecommerce_jsf.Entities.Produit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Named("panierBean")
@SessionScoped
@Getter
@Setter
public class PanierBean implements Serializable {

    private static final Logger logger = Logger.getLogger(PanierBean.class.getName());

    @Inject
    private UserBean userBean;

    @Inject
    private PanierDAO panierDAO;

    @Inject
    private LignePanierDAO  lignePanierDAO;

    @Inject
    private ProduitDAO produitDAO;

    private Panier panier;
    private List<LignePanier> lignes = new ArrayList<>();

    @PostConstruct
    @Transactional //  l'initialisation
    public void init() {
        logger.info("=== INITIALISATION PanierBean ===");
        if (userBean != null && userBean.getCurrentuser() != null) {
            try {
                panier = panierDAO.findByUser(userBean.getCurrentuser().getId_user());

                if (panier == null) {
                    logger.info("Création d'un nouveau panier");
                    panier = new Panier();
                    panier.setUser(userBean.getCurrentuser());
//                    panier.setDatecreation(new Date());
                    panier.setTotal(0.0);
                    panier.setLignes(new ArrayList<>());
                    panierDAO.create(panier);
                    lignes = new ArrayList<>();  // ← Panier vide
                    logger.info("✅ Nouveau panier créé ID: " + panier.getId_panier());
                } else {
                    //Charger depuis LignePanierDAO au lieu de panier.getLignes()
                    lignes = lignePanierDAO.findByPanierId(panier.getId_panier());
                    logger.info("✅ Panier chargé ID: " + panier.getId_panier() + " avec " + lignes.size() + " articles");
                }
            } catch (Exception e) {
                logger.severe("❌ ERREUR init Panier: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    @Transactional
    private void updatePanier() {
        if (panier != null) {
            try {
                // 1. Calculer le total
                double nouveauTotal = 0;
                for (LignePanier ligne : lignes) {
                    if (ligne.getProduit() != null) {
                        nouveauTotal += ligne.getProduit().getPrix() * ligne.getQuantite();
                    }
                }

                // 2. Mettre à jour le total du panier
                panier.setTotal(nouveauTotal);

                // 3. Mettre à jour les lignes
                panier.setLignes(lignes);

                // 4. Sauvegarder en base
                panierDAO.update(panier);

                logger.info("✅ Panier sauvegardé - Total: " + nouveauTotal + " DH");
            } catch (Exception e) {
                logger.severe("❌ ERREUR updatePanier: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void addToCart(Produit produit) {
        logger.info("=== AJOUT AU PANIER ===");

        if (produit == null) {
            logger.severe("❌ Produit null");
            return;
        }

        // Initialiser si nécessaire
        if (lignes == null) {
            lignes = new ArrayList<>();
        }

        // Rechercher le produit existant
        LignePanier ligneExistante = null;
        for (LignePanier ligne : lignes) {
            if (ligne.getProduit() != null &&
                    ligne.getProduit().getId_produit().equals(produit.getId_produit())) {
                ligneExistante = ligne;
                break;
            }
        }

        if (ligneExistante == null) {
            // Nouveau produit - vérifier stock
            if (produit.getStock() >= 1) {
                LignePanier nouvelleLigne = new LignePanier();
                nouvelleLigne.setProduit(produit);
                nouvelleLigne.setQuantite(1);
                nouvelleLigne.setPanier(panier);
                lignes.add(nouvelleLigne);

                // Mettre à jour le stock
                updateStock(produit, -1);
                logger.info("✅ NOUVEAU: " + produit.getNom() + " - Stock restant: " + produit.getStock());
            } else {
                logger.warning("❌ Stock insuffisant pour ajouter: " + produit.getNom());
            }
        } else {
            // Produit existant - vérifier stock avant d'augmenter
            if (produit.getStock() >= 1) {
                int ancienneQuantite = ligneExistante.getQuantite();
                ligneExistante.setQuantite(ancienneQuantite + 1);

                // Mettre à jour le stock
                updateStock(produit, -1);
                logger.info("✅ QUANTITÉ+: " + produit.getNom() + " = " + ligneExistante.getQuantite() + " - Stock restant: " + produit.getStock());
            } else {
                logger.warning("❌ Stock insuffisant pour augmenter: " + produit.getNom());
            }
        }

        updatePanier();
    }


    @Transactional
    private void updateStock(Produit produit, int quantiteDelta) {
        try {
            // Recharger le produit depuis la base pour avoir les données fraîches
            Produit produitActualise = produitDAO.findById(produit.getId_produit());
            if (produitActualise != null) {
                int nouveauStock = produitActualise.getStock() + quantiteDelta;

                // S'assurer que le stock ne devient pas négatif
                if (nouveauStock < 0) {
                    logger.severe("❌ ERREUR: Tentative de mettre le stock négatif pour: " + produit.getNom());
                    return;
                }

                produitActualise.setStock(nouveauStock);
                produitDAO.update(produitActualise);

                // Mettre à jour l'objet produit dans le bean
                produit.setStock(nouveauStock);

                logger.info("📦 Stock mis à jour: " + produit.getNom() +
                        " - Variation: " + quantiteDelta + " - Nouveau stock: " + nouveauStock);
            }
        } catch (Exception e) {
            logger.severe("❌ ERREUR lors de la mise à jour du stock: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Transactional
    public void remove(LignePanier ligne) {
        if (ligne != null && ligne.getProduit() != null) {
            Produit produit = ligne.getProduit();
            int quantiteRestituee = ligne.getQuantite();

            if (lignes.remove(ligne)) {
                // Restituer le stock
                updateStock(produit, quantiteRestituee);
                logger.info("✅ SUPPRIMÉ: " + produit.getNom() + " - Stock restitué: " + quantiteRestituee + " - Nouveau stock: " + produit.getStock());
                updatePanier();
            }
        }
    }

    @Transactional
    public void updateQuantity(LignePanier ligne, int quantite) {
        if (ligne == null || ligne.getProduit() == null) {
            return;
        }

        Produit produit = ligne.getProduit();
        int ancienneQuantite = ligne.getQuantite();
        int difference = quantite - ancienneQuantite;

        if (quantite <= 0) {
            remove(ligne);
        } else {
            // Vérifier si on peut augmenter la quantité
            if (difference > 0 && produit.getStock() < difference) {
                logger.warning("❌ Stock insuffisant pour modifier: " + produit.getNom() +
                        " - Demande: " + difference + " - Disponible: " + produit.getStock());
                return;
            }

            // Mettre à jour le stock
            updateStock(produit, -difference);
            ligne.setQuantite(quantite);

            logger.info("✅ QUANTITÉ MODIFIÉE: " + produit.getNom() +
                    " - Ancienne: " + ancienneQuantite + " - Nouvelle: " + quantite +
                    " - Stock restant: " + produit.getStock());

            updatePanier();
        }
    }


    public double getTotal() {
        if (lignes == null || lignes.isEmpty()) return 0.0;

        double total = lignes.stream()
                .filter(l -> l.getProduit() != null)
                .mapToDouble(l -> l.getProduit().getPrix() * l.getQuantite())
                .sum();
        return total;
    }


    }
