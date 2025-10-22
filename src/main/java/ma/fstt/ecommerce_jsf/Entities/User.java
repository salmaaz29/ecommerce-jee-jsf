package ma.fstt.ecommerce_jsf.Entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_user;

    @Column
    private String nom;
    @Column
    private String email;
    @Column
    private String mot_de_passe;
    @Column
    private String adresse;



    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Panier panier;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Commande> commandes;


    public User(String nom, String email, String mot_de_passe, String adresse) {
        this.nom = nom;
        this.email = email;
        this.mot_de_passe = mot_de_passe;
        this.adresse = adresse;

    }

}
