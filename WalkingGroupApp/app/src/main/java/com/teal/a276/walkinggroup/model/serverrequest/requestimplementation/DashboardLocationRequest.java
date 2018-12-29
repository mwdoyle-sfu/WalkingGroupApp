package com.teal.a276.walkinggroup.model.serverrequest.requestimplementation;

import android.support.annotation.NonNull;

import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.dataobjects.UserLocation;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.AbstractServerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

/**
 * Wrapper for requesting location from all monitors of the current user and their leaders. Returns the
 * result of these locations as a list of users to an activity. Can be initialized to make a timer based
 * request.
 */

public class DashboardLocationRequest extends AbstractServerRequest {
    private final long requestRate;
    private final Timer timer = new Timer();
    private final ArrayList<User> users = new ArrayList<>();

    public DashboardLocationRequest(User currentUser, long requestRate, @NonNull ServerError errorCallback) {
        super(currentUser, errorCallback);
        this.requestRate = requestRate;
        makeServerRequest();
    }

    @Override
    protected void makeServerRequest() {
        if (requestRate <= 0) {
            getMonitors();
        } else {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getMonitors();
                }
            }, requestRate, requestRate);
        }
    }

    private void getMonitors() {
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<List<User>> call = proxy.getMonitors(currentUser.getId(), 1L);
        ServerManager.serverRequest(call, this::monitorsResult, errorCallback);
    }

    private void monitorsResult(List<User> monitors) {
        for(User user: monitors) {
            ServerProxy proxy = ServerManager.getServerProxy();
            Call<UserLocation> call = proxy.getLastGpsLocation(user.getId());
            ServerManager.serverRequest(call,
                    result -> userLocationResult(
                            result,
                            user,
                            monitors.size() + user.getMemberOfGroups().size()
                    ), errorCallback);

            for(Group group : user.getMemberOfGroups()){
                ServerProxy proxyForGroup = ServerManager.getServerProxy();
                Call<User> callForGroup = proxyForGroup.getUserById(group.getLeader().getId(), null);
                ServerManager.serverRequest(callForGroup,
                        result -> leaderResult(
                                result,
                                monitors.size() + user.getMemberOfGroups().size()
                        ), errorCallback);
            }
        }
    }

    private void leaderResult(User user, int numberOfUsersOnDashBoard) {
        user.setLeader(true);
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<UserLocation> call = proxy.getLastGpsLocation(user.getId());
        ServerManager.serverRequest(call,
                result -> userLocationResult(result, user, numberOfUsersOnDashBoard), errorCallback);
    }

    private void userLocationResult(UserLocation location, User user, int numberOfUsersOnDashBoard) {
        user.setLocation(location);
        users.add(user);

        if(users.size() == numberOfUsersOnDashBoard) {
            setDataChanged(users);
            users.clear();
        }
    }

    public void unsubscribeFromUpdates() {
        timer.cancel();
    }
}
