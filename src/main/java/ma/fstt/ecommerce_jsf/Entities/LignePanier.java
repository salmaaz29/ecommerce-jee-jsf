package ma.fstt.ecommerce_jsf.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LignePanier")
@Setter
@Getter
@NoArgsConstructor
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_ligne;

    @Column
    private int quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panier_id", nullable = false)
    private Panier panier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    public LignePanier(int quantite, Panier panier, Produit produit) {
        this.quantite = quantite;
        this.panier = panier;
        this.produit = produit;
        // Synchronisation bidirectionnelle
        if (panier != null && !panier.getLignes().contains(this)) {
            panier.getLignes().add(this);
        }
    }
}