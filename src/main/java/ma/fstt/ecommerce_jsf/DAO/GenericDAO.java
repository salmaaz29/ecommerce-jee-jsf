package ma.fstt.ecommerce_jsf.DAO;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

public class GenericDAO<T> implements Serializable {

    private Class<T> entityClass;

    @PersistenceContext(unitName = "ecommercePU")
    protected EntityManager em;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }


    @Transactional
    public void create(T entity) {
        em.persist(entity);
    }

    @Transactional
    public void save(T entity) {
        em.persist(entity);
    }

    @Transactional
    public void update(T entity) {
        em.merge(entity);
    }

    @Transactional
    public void delete(Long id) {
        T entity = em.find(entityClass, id);
        if (entity != null) {
            em.remove(entity);
        }
    }

    public T findById(Long id) {
        return em.find(entityClass, id);
    }

    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }
}