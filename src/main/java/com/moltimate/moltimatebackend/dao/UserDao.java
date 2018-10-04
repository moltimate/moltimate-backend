package com.moltimate.moltimatebackend.dao;

import com.moltimate.moltimatebackend.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class UserDao {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public List<User> getUsers() {
        Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery criteria = builder.createQuery(User.class);
        Root contactRoot = criteria.from(User.class);
        criteria.select(contactRoot);
        return session.createQuery(criteria).getResultList();
    }

    public User saveUser(User user) {
        entityManagerFactory.unwrap(SessionFactory.class)
                            .openSession()
                            .save(user);
        return user;
    }
}


