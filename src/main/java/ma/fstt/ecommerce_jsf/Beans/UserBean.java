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
import ma.fstt.ecommerce_jsf.DAO.UserDAO;
import ma.fstt.ecommerce_jsf.Entities.User;

import java.io.Serializable;

@Getter
@Setter
@Named("userBean") // ✅ Changé en minuscule pour la cohérence
@SessionScoped
public class UserBean implements Serializable {

    @Inject
    private UserDAO userDAO;

    private User currentuser;
    private String email;
    private String mot_de_passe;
    private String nom;
    private String adresse ;

    @PostConstruct
    @Transactional // ✅
    public void init() {
        // Initialisation si nécessaire
    }

    // login
    @Transactional // ✅
    public String login() {

        boolean valid = userDAO.User(email, mot_de_passe);
        if (valid) {
            currentuser = userDAO.findByEmail(email);
            if("admin@gmail.com".equalsIgnoreCase(currentuser.getEmail())){
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Login Successful de l'admin", "Bienvenue " + currentuser.getNom()));
                return "dashboard_admin.xhtml?faces-redirect=true";
            }else{
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Login Successful", "Bienvenue " + currentuser.getNom()));
                return "produits.xhtml?faces-redirect=true";
            }

        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Email ou mot de passe incorrect"));
            return null;
        }
    }

    // verification qu'il est un admin
    public boolean isAdmin() {
        return currentuser != null && "admin@gmail.com".equalsIgnoreCase(currentuser.getEmail());
    }

    // inscription
    @Transactional // ✅
    public String register() { // ✅ Changé en minuscule pour la convention Java
        try {
            User existeuser = userDAO.findByEmail(email);
            if (existeuser != null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Cet email existe déjà"));
                return null;
            }

            User newuser = new User();
            newuser.setNom(nom);
            newuser.setEmail(email);
            newuser.setMot_de_passe(mot_de_passe);
            newuser.setAdresse(adresse);
            userDAO.create(newuser);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Compte créé ! Connectez-vous."));
            return "login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Échec de l'inscription : " + e.getMessage()));
            return null;
        }
    }

    // logout
    @Transactional // ✅
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml?faces-redirect=true";
    }

    // vérifier si le user est connecté ou pas
    // ❌ Pas besoin de @Transactional sur les getters
    public boolean isLogged() {
        return currentuser != null;
    }
}