package ma.fstt.ecommerce_jsf.DAO;


import jakarta.persistence.EntityManager;
import ma.fstt.ecommerce_jsf.Entities.Panier;

public class PanierDAO extends GenericDAO<Panier> {

    public PanierDAO() {
        super(Panier.class);
    }

    public Panier findByUser(Long USERID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Panier p WHERE p.user.id_user = :usertId ORDER BY p.datecreation DESC",
                            Panier.class)
                    .setParameter("usertId", USERID)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
}