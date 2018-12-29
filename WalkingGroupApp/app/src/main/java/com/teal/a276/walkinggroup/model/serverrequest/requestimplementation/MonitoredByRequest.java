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
 * ServerRequest for setting up a monitoring relationship between the current user and the user with
 * the email.
 */
public class MonitoredByRequest extends AbstractServerRequest {
    private final String monitorsEmail;

    public MonitoredByRequest(User currentUser, String monitorsEmail, @NonNull ServerError errorCallback) {
        super(currentUser, errorCallback);
        this.monitorsEmail = monitorsEmail;
    }

    @Override
    public void makeServerRequest() {
        getUserForEmail(monitorsEmail, this::userResult, null);
    }

    private void userResult(User user) {
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<List<User>> call = proxy.monitoredByUser(this.currentUser.getId(), user);
        ServerManager.serverRequest(call, this::monitorsResult, this.errorCallback);
    }

    private void monitorsResult(List<User> users) {
        setDataChanged(users);
    }
}
