package org.moltimate.moltimatebackend.user;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "/{email}", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUserByEmail(@PathVariable(value = "lastName") String email) {
        return new ResponseEntity<>(userService.getUsersByEmail(email), HttpStatus.OK);
    }

    @RequestMapping(value = "/debug", method = RequestMethod.GET)
    public ResponseEntity<List<User>> createDummyData() {
        ImmutableMap.<String, String>builder()
                .put("Paul_Craig@rit.edu", "password")
                .put("Herbert_Bernstein@rit.edu", "password")
                .put("Larry_Kiser@rit.edu", "password")
                .put("Josh_Miller@rit.edu", "password")
                .put("Steve_Teplica@rit.edu", "password")
                .put("Shannon_McIntosh@rit.edu", "password")
                .put("George_Herde@rit.edu", "password")
                .put("Michael_Teplica@rit.edu", "password")
                .build()
                .forEach((email, password) -> {
                    User newUser = User.builder()
                                       .email(email)
                                       .password(password)
                                       .build();
                    userService.saveUser(newUser);
                });
        return getUsers();
    }
}
