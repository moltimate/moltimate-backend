package com.moltimate.moltimatebackend.service;

import com.moltimate.moltimatebackend.dao.UserDao;
import com.moltimate.moltimatebackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public User saveUser(User user) {
        return userDao.saveUser(user);
    }
}
