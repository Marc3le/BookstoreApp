package com.example.coursework.hibernateControllers;

import com.example.coursework.model.Book;
import com.example.coursework.model.Manga;
import com.example.coursework.model.Publication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.ArrayList;
import java.util.List;

public class GenericHibernate {
    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    public GenericHibernate(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    protected EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public <T> void create(T entity) {
        try {
            entityManager = createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public <T> void update(T entity) {
        try {
            entityManager = createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public <T> void delete(Class<T> entityClass, int id) {
        try {
            entityManager = createEntityManager();
            entityManager.getTransaction().begin();
            T publication = entityManager.find(entityClass, id);
            entityManager.remove(publication);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

//    public List<Book> getAllBooks(){
//        List<Book> list = new ArrayList<>();
//        try{
//            entityManager = createEntityManager();
//            CriteriaQuery query = entityManager.getCriteriaBuilder().createQuery();
//            query.select(query.from(Book.class));
//            Query q = entityManager.createQuery(query);
//            list = q.getResultList();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return list;
//    }

    public <T> List<T> getAllRecords(Class<T> entityClass) {
        List<T> list = new ArrayList<>();
        try {
            entityManager = createEntityManager();
            CriteriaQuery query = entityManager.getCriteriaBuilder().createQuery();
            query.select(query.from(entityClass));
            Query q = entityManager.createQuery(query);
            list = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public <T> T getEntityById(Class<T> entityClass, int id){
        T result = null;
        try{
            entityManager = createEntityManager();
            entityManager.getTransaction().begin();
            result = entityManager.find(entityClass, id);
            entityManager.getTransaction().commit();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
