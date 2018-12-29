package com.teal.a276.walkinggroup.model.serverrequest;

import android.support.annotation.NonNull;

import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverproxy.ServerResult;

import java.util.Observable;

import retrofit2.Call;

/**
 * Abstract idea of a server request. This class encapsulated the idea of multiple chained server request to
 * produce a single unified output.
 */
public abstract class AbstractServerRequest extends Observable {
    protected final ServerError errorCallback;
    protected final User currentUser;

    protected AbstractServerRequest(User currentUser, @NonNull ServerError errorCallback) {
        this.currentUser = currentUser;
        this.errorCallback = errorCallback;
    }

    protected abstract void makeServerRequest();

    protected void getUserForEmail(String email,
                                   @NonNull final ServerResult<User> resultCallback,
                                   Long depth) {
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<User> userByEmailCall = proxy.getUserByEmail(email, depth);
        ServerManager.serverRequest(userByEmailCall, resultCallback, errorCallback);
    }

    protected <T> void setDataChanged(T data) {
        setChanged();
        notifyObservers(data);
    }
}
