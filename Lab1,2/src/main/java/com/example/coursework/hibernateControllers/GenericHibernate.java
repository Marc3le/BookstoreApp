package com.example.coursework.hibernateControllers;

import com.example.coursework.model.Book;
import com.example.coursework.model.Manga;
import com.example.coursework.model.Publication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class GenericHibernate {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public GenericHibernate(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public <T> void create(T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void update(T entity) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void delete(Class<T> entityClass, int id) {
        try {
            entityManager.getTransaction().begin();
            T publication = entityManager.find(entityClass, id);
            entityManager.remove(publication);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
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
            result = entityManager.find(entityClass, id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
