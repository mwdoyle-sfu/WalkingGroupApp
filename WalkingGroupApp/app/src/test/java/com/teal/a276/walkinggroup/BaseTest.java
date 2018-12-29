package com.teal.a276.walkinggroup;

import com.teal.a276.walkinggroup.model.dataobjects.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for shared test helper methods
 */

abstract class BaseTest {

    List<User> generateUserList() {
        User user = new User();
        user.setName("testUser");
        ArrayList<User> users = new ArrayList<>();
        users.add(user);

        return users;
    }
}
