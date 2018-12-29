package com.teal.a276.walkinggroup.model.dataobjects.permissions;

import com.teal.a276.walkinggroup.model.dataobjects.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Authorizor class to store the data the server expects and returns.
 * An authorizor is a nested object in the permissions json
 */

public class Authorizor {
    private List<User> users = new ArrayList<>();
    private String status;
    private User whoApprovedOrDenied;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getWhoApprovedOrDenied() {
        return whoApprovedOrDenied;
    }

    public void setWhoApprovedOrDenied(User whoApprovedOrDenied) {
        this.whoApprovedOrDenied = whoApprovedOrDenied;
    }
}
