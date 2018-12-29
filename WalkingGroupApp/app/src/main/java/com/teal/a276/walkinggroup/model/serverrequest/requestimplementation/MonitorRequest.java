package com.teal.a276.walkinggroup.model.serverrequest.requestimplementation;

import android.support.annotation.NonNull;

import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.AbstractServerRequest;

import java.util.List;

import retrofit2.Call;

/**
 * Monitor request to set up a relationship so the current user monitors the user with the given email.
 */
public class MonitorRequest extends AbstractServerRequest {
    private final String userToMonitorEmail;

    public MonitorRequest(User currentUser, String userToMonitorEmail, @NonNull ServerError errorCallback) {
        super(currentUser, errorCallback);
        this.userToMonitorEmail = userToMonitorEmail;
    }

    @Override
    public void makeServerRequest() {
        getUserForEmail(userToMonitorEmail, this::userResult, null);
    }

    private void userResult(User user) {
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<List<User>> call = proxy.monitorUser(this.currentUser.getId(), user);
        ServerManager.serverRequest(call, this::monitorsResult, this.errorCallback);
    }

    private void monitorsResult(List<User> users) {
        setDataChanged(users);
    }
}
