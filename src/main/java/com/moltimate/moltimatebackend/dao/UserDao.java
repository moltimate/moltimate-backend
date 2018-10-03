package com.moltimate.moltimatebackend.dao;

import com.moltimate.moltimatebackend.model.UserDetails;

import java.util.List;

public interface UserDao {

    List<UserDetails> getUserDetails();

    UserDetails saveUser(UserDetails userDetails);
}
