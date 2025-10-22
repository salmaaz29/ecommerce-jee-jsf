package ma.fstt.ecommerce_jsf.DAO;

import jakarta.persistence.EntityManager;
import ma.fstt.ecommerce_jsf.Entities.User;

public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public User findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public boolean authenticate(String email, String password) {
        User user = findByEmail(email);
        return user != null && user.getMot_de_passe().equals(password);
    }
}