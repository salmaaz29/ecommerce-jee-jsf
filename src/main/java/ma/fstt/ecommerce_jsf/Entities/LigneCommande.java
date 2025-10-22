package ma.fstt.ecommerce_jsf.Entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="LigneCommande")
@Getter
@Setter
@NoArgsConstructor
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column
    private int quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="commande_id", nullable = false)
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="produit_id", nullable = false)
    private Produit produit;

    public LigneCommande(int quantite, Commande commande, Produit produit) {
        this.quantite = quantite;
        this.commande = commande;
        this.produit = produit;
        if(commande != null) {
            commande.getLignes().add(this);
        }

    }

}
