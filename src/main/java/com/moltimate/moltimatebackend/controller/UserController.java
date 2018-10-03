package com.moltimate.moltimatebackend.controller;

import com.google.common.collect.ImmutableMap;
import com.moltimate.moltimatebackend.model.UserDetails;
import com.moltimate.moltimatebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<List<UserDetails>> userDetails() {
        List<UserDetails> userDetails = userService.getUserDetails();
        return new ResponseEntity<List<UserDetails>>(userDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "/debug", method = RequestMethod.GET)
    public ResponseEntity<String> createDummyData() {
        ImmutableMap.<String, String>builder()
                .put("Josh", "Miller")
                .put("Steve", "Teplica")
                .put("Shannon", "McIntosh")
                .put("George", "Herde")
                .put("Paul", "Craig")
                .put("Herbert", "Bernstein")
                .put("Larry", "Kiser")
                .build()
                .forEach((firstName, lastName) -> userService.saveUser(new UserDetails(firstName, lastName)));
        return new ResponseEntity<String>("Created dummy data!", HttpStatus.OK);
    }
}
