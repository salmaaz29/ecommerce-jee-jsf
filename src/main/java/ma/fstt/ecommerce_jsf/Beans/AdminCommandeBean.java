package ma.fstt.ecommerce_jsf.Beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import ma.fstt.ecommerce_jsf.DAO.CommandeDAO;
import ma.fstt.ecommerce_jsf.Entities.Commande;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Named("adminCommandeBean")
@ViewScoped
public class AdminCommandeBean implements Serializable {

    private static final Logger logger = Logger.getLogger(AdminCommandeBean.class.getName());

    @Inject
    private CommandeDAO commandeDAO;

    @Inject
    private UserBean userBean;

    private List<Commande> commandes;

    @PostConstruct
    @Transactional
    public void init() {
        loadCommandes();
    }

    @Transactional
    public List<Commande> getCommandes() {
        return commandes;
    }

    @Transactional
    private void loadCommandes() {
        try {
            commandes = commandeDAO.findAll();
            logger.info("✅ Loaded " + commandes.size() + " commands for admin");
        } catch (Exception e) {
            logger.severe("❌ Error loading admin commands: " + e.getMessage());
            commandes = new ArrayList<>();
        }
    }

    // ✅ Mise à jour automatique avec AJAX
    @Transactional
    public void updateStatutAjax(Long commandeId, String nouveauStatut) {
        try {
            logger.info("🔄 Mise à jour AJAX - ID: " + commandeId + ", Statut: " + nouveauStatut);

            // Validation
            if (commandeId == null || nouveauStatut == null) {
                logger.severe("❌ Paramètres manquants");
                return;
            }

            // Vérifier le statut valide
            if (!List.of("EN_COURS", "VALIDEE", "LIVREE", "ANNULEE").contains(nouveauStatut)) {
                logger.severe("❌ Statut non valide: " + nouveauStatut);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Statut non valide: " + nouveauStatut));
                return;
            }

            // Mise à jour en base
            int result = commandeDAO.updateStatut(commandeId, nouveauStatut);

            if (result > 0) {
                logger.info("✅ SUCCÈS: Statut mis à jour pour commande: " + commandeId);

                // Recharger les données pour refléter le changement
                loadCommandes();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès",
                                "Statut de la commande #" + commandeId + " mis à jour: " + nouveauStatut));
            } else {
                logger.severe("❌ ÉCHEC: Aucune ligne mise à jour");
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Échec de la mise à jour"));
            }

        } catch (Exception e) {
            logger.severe("❌ ERREUR lors de la mise à jour AJAX: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
                            "Erreur technique: " + e.getMessage()));
        }
    }

    @Transactional
    public void updateStatutDirect() {
        try {
            // Récupérer les paramètres de la requête
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();

            Long commandeId = Long.parseLong(params.get("commandeId"));
            String nouveauStatut = params.get("nouveauStatut");

            updateStatutAjax(commandeId, nouveauStatut);

        } catch (Exception e) {
            logger.severe("❌ Erreur updateStatutDirect: " + e.getMessage());
        }
    }
}