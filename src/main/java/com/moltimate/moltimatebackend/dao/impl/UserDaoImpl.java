package com.moltimate.moltimatebackend.dao.impl;

import com.moltimate.moltimatebackend.dao.UserDao;
import com.moltimate.moltimatebackend.model.UserDetails;
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
public class UserDaoImpl implements UserDao {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public List getUserDetails() {
        Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery criteria = builder.createQuery(UserDetails.class);
        Root contactRoot = criteria.from(UserDetails.class);
        criteria.select(contactRoot);
        return session.createQuery(criteria).getResultList();
    }

    public UserDetails saveUser(UserDetails userDetails) {
        Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
        session.save(userDetails);
        return userDetails;
    }
}


