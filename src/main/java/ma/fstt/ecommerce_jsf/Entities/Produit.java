package ma.fstt.ecommerce_jsf.Entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="Produit")
@Getter
@Setter
@NoArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_produit;

    @Column
    private String nom;
    @Column
    private String description;
    @Column
    private String image;
    @Column
    private Double prix;
    @Column
    private int stock;


    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    private List<LignePanier> lignes;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    private List<LigneCommande> ligneCommandes;

    public Produit(String nom, String description, String image, Double prix, int stock) {
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.prix = prix;
        this.stock = stock;

    }


}
