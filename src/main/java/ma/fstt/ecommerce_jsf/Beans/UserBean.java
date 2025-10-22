package ma.fstt.ecommerce_jsf.Beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import ma.fstt.ecommerce_jsf.DAO.UserDAO;
import ma.fstt.ecommerce_jsf.Entities.User;

import java.io.Serializable;

@Getter
@Setter
@Named("UserBean")
@SessionScoped
public class UserBean implements Serializable {
    @Inject
    private UserDAO userDAO;

    private User currentuser;
    private String email;
    private String mot_de_passe;
    private String nom ;


    // login

    public String login(){

        boolean valid = userDAO.User(email, mot_de_passe);
        if(valid){
            currentuser = userDAO.findByEmail(email);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Login Successful", "Bienvenue"+ currentuser.getNom()));
            return "/produits.xhtml?faces-redirect=true";
        }else{
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Email ou mot de passe incorrect"));
            return null;
        }
    }

    // inscription

    public String Register(){
        User existeuser = userDAO.findByEmail(email);
        if(existeuser != null){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "cet eamil deja existe"));
            return null;
        }

        User newuser = new User();
        newuser.setNom(nom);
        newuser.setEmail(email);
        newuser.setMot_de_passe(mot_de_passe);
        userDAO.create(newuser);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Compte créé ! Connectez-vous."));
        return "/login.xhtml?faces-redirect=true";
    }


    // logout

    public String Logout(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }

    // verifier si le user est connecte ou pas
    public boolean isLogged() {
        return currentuser != null;
    }

}
