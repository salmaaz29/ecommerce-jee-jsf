package ma.fstt.ecommerce_jsf.Entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "panier")
@Getter
@Setter
@NoArgsConstructor
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_panier;
    @Temporal(TemporalType.DATE)
    private Date datecreation;
    @Column
    private Double total;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LignePanier> lignes;

    public Panier(Date datecreation, Double total, User user) {
        this.datecreation = datecreation;
        this.total = total;
        this.user = user;
    }
}