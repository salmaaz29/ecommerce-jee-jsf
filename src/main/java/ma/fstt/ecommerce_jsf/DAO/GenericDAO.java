package ma.fstt.ecommerce_jsf.DAO;

import jakarta.persistence.EntityManager;

import java.io.Serializable;
import java.util.List;

public class GenericDAO<T> implements Serializable {

        private Class<T> entityClass;

        public GenericDAO(Class<T> entityClass) {
            this.entityClass = entityClass;
        }

        public void create(T entity) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                em.persist(entity);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                e.printStackTrace();
            } finally {
                em.close();
            }
        }

        public T findById(Long id) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                return em.find(entityClass, id);
            } finally {
                em.close();
            }
        }

        public List<T> findAll() {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                        .getResultList();
            } finally {
                em.close();
            }
        }

        public void update(T entity) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                em.merge(entity);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                e.printStackTrace();
            } finally {
                em.close();
            }
        }

        public void delete(Long id) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                T entity = em.find(entityClass, id);
                if (entity != null) {
                    em.remove(entity);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                e.printStackTrace();
            } finally {
                em.close();
            }
        }
    }
