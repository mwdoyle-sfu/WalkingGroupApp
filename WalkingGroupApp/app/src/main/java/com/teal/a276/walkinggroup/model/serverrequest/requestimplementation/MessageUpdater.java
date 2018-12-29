package com.teal.a276.walkinggroup.model.serverrequest.requestimplementation;

import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.RequestConstant;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

/**
 * Class to handle polling the server for message updates. When created it starts polling the server.
 */

public class MessageUpdater extends Observable {
    private final Timer timer = new Timer();
    final private int UPDATE_RATE = 60000;

    public MessageUpdater(final User user, final ServerError errorCallback) {
        subscribeForUpdates(user, errorCallback);
    }

    private void subscribeForUpdates(final User user, final ServerError errorCallback) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getMessages(user, errorCallback);
            }
        }, UPDATE_RATE, UPDATE_RATE);
    }

    private void getMessages(User user, ServerError errorCallback) {
        HashMap<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(RequestConstant.FOR_USER, user.getId());
        requestParameters.put(RequestConstant.STATUS, RequestConstant.UNREAD);
        ServerProxy proxy = ServerManager.getServerProxy();

        Call<List<Message>> call = proxy.getMessages(requestParameters);
        ServerManager.serverRequest(call, this::unreadMessages, errorCallback);
    }

    private void unreadMessages(List<Message> messages) {
            setChanged();
            notifyObservers(messages);
        }

    public void unsubscribeFromUpdates() {
        timer.cancel();
    }

    @Override
    protected void finalize() throws Throwable {
        timer.cancel();
    }
}
