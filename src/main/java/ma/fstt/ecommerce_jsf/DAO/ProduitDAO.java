package ma.fstt.ecommerce_jsf.DAO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import ma.fstt.ecommerce_jsf.Entities.Produit;

import java.util.List;

@Named
@ApplicationScoped
public class ProduitDAO extends GenericDAO<Produit> {

    public ProduitDAO() {
        super(Produit.class);
    }

    @Transactional
    public List<Produit> searchByNom(String keyword) {
        return em.createQuery("SELECT p FROM Produit p WHERE p.nom LIKE :keyword", Produit.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }
}