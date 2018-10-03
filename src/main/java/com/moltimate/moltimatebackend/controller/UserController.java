package com.moltimate.moltimatebackend.controller;

import com.google.common.collect.ImmutableMap;
import com.moltimate.moltimatebackend.model.User;
import com.moltimate.moltimatebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/debug", method = RequestMethod.GET)
    public ResponseEntity<List<User>> createDummyData() {
        ImmutableMap.<String, String>builder()
                .put("Paul", "Craig")
                .put("Herbert", "Bernstein")
                .put("Larry", "Kiser")
                .put("Josh", "Miller")
                .put("Steve", "Teplica")
                .put("Shannon", "McIntosh")
                .put("George", "Herde")
                .build()
                .forEach((firstName, lastName) -> userService.saveUser(new User(firstName, lastName)));
        return getUsers();
    }
}
