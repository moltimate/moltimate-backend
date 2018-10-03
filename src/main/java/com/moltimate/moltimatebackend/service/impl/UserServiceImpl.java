package com.moltimate.moltimatebackend.service.impl;

import com.moltimate.moltimatebackend.dao.UserDao;
import com.moltimate.moltimatebackend.model.UserDetails;
import com.moltimate.moltimatebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    public List<UserDetails> getUserDetails() {
        return userDao.getUserDetails();
    }

    public UserDetails saveUser(UserDetails userDetails) {
        return userDao.saveUser(userDetails);
    }
}
