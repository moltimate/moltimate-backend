package com.moltimate.moltimatebackend.service;

import com.moltimate.moltimatebackend.model.UserDetails;

import java.util.List;

public interface UserService {

    List<UserDetails> getUserDetails();

    UserDetails saveUser(UserDetails userDetails);
}
