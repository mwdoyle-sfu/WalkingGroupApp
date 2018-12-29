package com.teal.a276.walkinggroup.model;

import android.content.res.Resources;

import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import java.util.List;

/**
 * Singleton for accessing groups/users
 */

public class ModelFacade {

    private static ModelFacade instance;
    private User currentUser = null;
    private GroupManager groupManager = null;
    private Resources appResources = null;
    private List<User> users = null;

    private ModelFacade() {}

    public static ModelFacade getInstance() {
        if(instance == null) {
            instance = new ModelFacade();
        }
        return instance;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("User was not set before calling getUser");
        }

        return currentUser;
    }

    public void setCurrentUser(User newCurrentUser) {
        this.currentUser = newCurrentUser;
    }

    public GroupManager getGroupManager() {
        if (groupManager == null) {
            throw new IllegalStateException("GroupManger was not set before calling getGroupManager");
        }

        return groupManager;
    }

    public void setGroupManager(GroupManager newGroupManager) {
        this.groupManager = newGroupManager;
    }

    public void setAppResources(Resources resources) {
        appResources = resources;
    }

    public Resources getAppResources() {
        if (appResources == null) {
            throw new IllegalStateException("AppResources was not set before calling getAppResources");
        }

        return appResources;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
