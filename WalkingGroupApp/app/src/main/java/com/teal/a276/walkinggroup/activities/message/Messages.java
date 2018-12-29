package com.teal.a276.walkinggroup.activities.message;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.MessageUpdater;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.RequestConstant;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;

/**
 * Activity that handles displaying all read and unread messages the user has received
 */
public class Messages extends BaseActivity implements Observer {
    private RecyclerView unreadMessagesView;
    private ArrayAdapter<String> readMessagesAdapter;
    private ListView readMessagesView;
    private List<Message> unreadMessages = new ArrayList<>();
    private MessageUpdater messageUpdater;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        user = ModelFacade.getInstance().getCurrentUser();
        unreadMessagesView = findViewById(R.id.unreadMessages);
        readMessagesView = findViewById(R.id.readMessages);

        unreadMessagesView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.SimpleCallback messageTouchHelper = new MessageTouchHelper(0, ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Message message = unreadMessages.get(viewHolder.getAdapterPosition());
                ServerProxy proxy = ServerManager.getServerProxy();
                Call<User> call = proxy.setMessageRead(message.getId(), user.getId(), true);
                ServerManager.serverRequest(call,
                        result -> messageMarkedAsRead(result, viewHolder.getAdapterPosition()),
                        error -> resetList(error, viewHolder.getAdapterPosition()));
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(messageTouchHelper);
        helper.attachToRecyclerView(unreadMessagesView);

        Call<List<Message>> call = requestMessages(user.getId(), RequestConstant.UNREAD);
        ServerManager.serverRequest(call, this::unreadMessagesResult, this::error);

        call = requestMessages(user.getId(), RequestConstant.READ);
        ServerManager.serverRequest(call, this::readMessagesResult, this::error);
    }

    private Call<List<Message>> requestMessages(Long userId, String status) {
        HashMap<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(RequestConstant.FOR_USER, userId);
        requestParameters.put(RequestConstant.STATUS, status);
        ServerProxy proxy = ServerManager.getServerProxy();

        return proxy.getMessages(requestParameters);
    }

    private void unreadMessagesResult(List<Message> messages) {
        this.unreadMessages = messages;
        RecyclerView.Adapter adapter = new MessagesAdapter(messages);
        unreadMessagesView.setAdapter(adapter);
    }

    private void readMessagesResult(List<Message> messages) {
        readMessagesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messagesToStringList(messages));
        readMessagesView.setAdapter(readMessagesAdapter);
    }

    private List<String> messagesToStringList(List<Message> messages) {
        ArrayList<String> messagesText = new ArrayList<>(messages.size());
        for(Message message : messages) {
            messagesText.add(message.getText());
        }

        return messagesText;
    }

    private void messageMarkedAsRead(User user, int messageIndex) {
        Message readMessage = this.unreadMessages.get(messageIndex);
        MessagesAdapter adapter = (MessagesAdapter) unreadMessagesView.getAdapter();
        adapter.removeAt(messageIndex);

        readMessagesAdapter.add(readMessage.getText());
        readMessagesAdapter.notifyDataSetChanged();
        readMessagesView.invalidateViews();
    }

    private void resetList(String error, int messageIndex) {
        Message swipedMessage = this.unreadMessages.get(messageIndex);
        MessagesAdapter adapter = (MessagesAdapter) unreadMessagesView.getAdapter();
        adapter.removeAt(messageIndex);
        adapter.addMessageAt(swipedMessage, messageIndex);

        super.error(error);
    }

    @Override
    public void onPause() {
        super.onPause();
        messageUpdater.unsubscribeFromUpdates();
        messageUpdater.deleteObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        messageUpdater = new MessageUpdater(user, this::error);
        messageUpdater.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        List<Message> messages = (List<Message>)o;
        MessagesAdapter adapter = (MessagesAdapter) unreadMessagesView.getAdapter();
        adapter.clear();
        adapter.addAll(messages);
    }
}
