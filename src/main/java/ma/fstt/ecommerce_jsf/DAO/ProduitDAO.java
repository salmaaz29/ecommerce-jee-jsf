package ma.fstt.ecommerce_jsf.DAO;

import jakarta.persistence.EntityManager;
import ma.fstt.ecommerce_jsf.Entities.Produit;

import java.util.List;

public class ProduitDAO extends GenericDAO<Produit> {

    public ProduitDAO() {
        super(Produit.class);
    }

    public List<Produit> searchByNom(String keyword) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Produit p WHERE p.nom LIKE :keyword", Produit.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }
}