package com.moltimate.moltimatebackend.service;

import com.google.common.collect.ImmutableMap;
import com.moltimate.moltimatebackend.dao.UserDao;
import com.moltimate.moltimatebackend.model.User;
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
    private UserDao mockUserDao;

    @Before
    public void setup() {
        List<User> mockGetUsersResponse =  ImmutableMap.<String, String>builder()
                .put("Paul", "Craig")
                .put("Herbert", "Bernstein")
                .put("Larry", "Kiser")
                .put("Josh", "Miller")
                .put("Steve", "Teplica")
                .put("Shannon", "McIntosh")
                .put("George", "Herde")
                .build()
                .entrySet()
                .stream()
                .map((user) -> new User(user.getKey(), user.getValue()))
                .collect(Collectors.toList());
        when(mockUserDao.getUsers()).thenReturn(mockGetUsersResponse);
    }

    @Test
    public void getUsers_DoesUserExist() {
        List<User> users = userService.getUsers();
        Assert.assertTrue(users.stream().anyMatch((user) -> "Paul".equals(user.getFirstName()) && "Craig".equals(user.getLastName())));
    }

    @Test
    public void getUsers_DoesFakeUserExist() {
        List<User> users = userService.getUsers();
        Assert.assertFalse(users.stream().anyMatch((user) -> "Pauline".equals(user.getFirstName()) && "Craig".equals(user.getLastName())));
    }
}
