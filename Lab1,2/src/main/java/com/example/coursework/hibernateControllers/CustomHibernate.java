package com.example.coursework.hibernateControllers;

import com.example.coursework.model.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class CustomHibernate extends GenericHibernate {
    public CustomHibernate(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByLoginAndPsw(String login, String password) {
        User user = null;
        try {
            entityManager = createEntityManager();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root).where(criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("login"), login),
                    criteriaBuilder.equal(root.get("password"), password)
            ));

            Query q = entityManager.createQuery(query);
            user = (User) q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }


}
