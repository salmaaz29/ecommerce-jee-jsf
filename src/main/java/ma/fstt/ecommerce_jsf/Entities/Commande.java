package ma.fstt.ecommerce_jsf.Entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="Commande")
@Getter
@Setter
@NoArgsConstructor
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_commande;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCommande;

    @Column
    private double total; // Total de la commande, calculé à partir des lignes

    @Column
    private String statut;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<LigneCommande> lignes;

    public Commande(Date dateCommande, double total, User user,String statut) {
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.total = total;
        this.user = user;
    }


}
