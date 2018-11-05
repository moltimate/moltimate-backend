package org.moltimate.moltimatebackend.service;

import com.google.common.collect.ImmutableMap;
import org.moltimate.moltimatebackend.user.User;
import org.moltimate.moltimatebackend.user.UserRepository;
import org.moltimate.moltimatebackend.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository mockUserRepository;

    @Before
    public void setup() {
        List<User> mockGetUsersResponse = ImmutableMap.<String, String>builder()
                .put("Paul_Craig@rit.edu", "password")
                .put("Herbert_Bernstein@rit.edu", "password")
                .put("Larry_Kiser@rit.edu", "password")
                .put("Josh_Miller@rit.edu", "password")
                .put("Steve_Teplica@rit.edu", "password")
                .put("Shannon_McIntosh@rit.edu", "password")
                .put("George_Herde@rit.edu", "password")
                .put("Michael_Teplica@rit.edu", "password")
                .build()
                .entrySet()
                .stream()
                .map((user) -> User.builder()
                                   .email(user.getKey())
                                   .password(user.getValue())
                                   .build())
                .collect(Collectors.toList());
        when(mockUserRepository.findAll()).thenReturn(mockGetUsersResponse);
    }

    @Test
    public void getUsers_DoesUserExist() {
        List<User> users = userService.getUsers();
        Assert.assertTrue(users.stream().anyMatch((user) -> "Paul_Craig@rit.edu".equals(user.getEmail())));
    }

    @Test
    public void getUsers_DoesFakeUserExist() {
        List<User> users = userService.getUsers();
        Assert.assertFalse(users.stream().anyMatch((user) -> "Paulina_Craig@rit.edu".equals(user.getEmail())));
    }
}
