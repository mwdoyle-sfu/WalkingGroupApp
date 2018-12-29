package com.teal.a276.walkinggroup.activities.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.google.android.gms.maps.model.Marker;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.message.Messages;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.dataobjects.UserLocation;
import com.teal.a276.walkinggroup.model.serverproxy.RequestConstant;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.DashboardLocationRequest;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.MessageUpdater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

/**
 * Dashboard Activity for parents to check location of their children
 * and the location of the leader.
 *
 */

public class DashBoard extends AbstractMapActivity implements Observer{
    private final long MAP_UPDATE_RATE = 30000;
    private User user;
    private String messageCount;
    private Button msgButton;
    private MessageUpdater messageUpdater;
    private DashboardLocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        initializeMap(R.id.dashboardMap, MAP_UPDATE_RATE, MAP_UPDATE_RATE);

        user = ModelFacade.getInstance().getCurrentUser();
        messageCount = getString(R.string.dashboard_unread_message);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        setUpMsgButton();
        placeCurrentLocationMarker();

        // First call to populate pins before timer starts
        DashboardLocationRequest initialLocationRequest = new DashboardLocationRequest(user, 0, this::error);
        initialLocationRequest.addObserver(this);

        locationRequest = new DashboardLocationRequest(user, MAP_UPDATE_RATE, this::error);
        locationRequest.addObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        messageUpdater.unsubscribeFromUpdates();
        messageUpdater.deleteObserver(this);

        locationRequest.unsubscribeFromUpdates();
        locationRequest.deleteObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        messageUpdater = new MessageUpdater(user, this::error);
        messageUpdater.addObserver(this);

        locationRequest = new DashboardLocationRequest(user, MAP_UPDATE_RATE, this::error);
        locationRequest.addObserver(this);
    }

    private void setUpMsgButton() {
        msgButton = findViewById(R.id.dashMsgBtn);
        getServerMessageCount();
        msgButton.setOnClickListener(v -> startActivity(new Intent(this, Messages.class)));
    }

    private void getServerMessageCount(){
        HashMap<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(RequestConstant.STATUS, RequestConstant.UNREAD);
        requestParameters.put(RequestConstant.FOR_USER, user.getId());
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<List<Message>> call = proxy.getMessages(requestParameters);

        ServerManager.serverRequest(call, this::updateUnreadMsg, this::error);
    }
    private void updateUnreadMsg(List<Message> messages){
        String unreadCount = String.valueOf(messages.size());

        messageCount = getString(R.string.dashboard_unread_message) + unreadCount;
        msgButton.setText(messageCount);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, DashBoard.class);
    }

    protected void error(String error){
        super.error(error);
    }

    private String generateMarkerTitle(UserLocation location, String name) {
        String timeStamp = "";
        try {
            timeStamp = generateTimeCode(location.getTimestamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return name + getString(R.string.last_time_update) + timeStamp;
    }

    private void placeDashBoardMarker(UserLocation location, String name, MarkerColor color) {
        if(location.getLat() == null) {
            return;
        }

        String markerTitle = generateMarkerTitle(location, name);
        placeMarker(locationToLatLng(location) ,markerTitle, color);
    }

    private String generateTimeCode(String timestamp) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        Date date = format.parse(timestamp);
        Long timeSince = System.currentTimeMillis() - date.getTime();
        return String.format(Locale.getDefault(), getString(R.string.time_format),
                TimeUnit.MILLISECONDS.toMinutes(timeSince),
                TimeUnit.MILLISECONDS.toSeconds(timeSince) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSince))
        );
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o == messageUpdater) {
            updateUnreadMsg((List<Message>) arg);
        }
        else {
            addMarkersForUsers((List<User>) arg);
        }
    }

    private void addMarkersForUsers(List<User> users) {
        map.clear();
        for(User user : users) {
            placeDashBoardMarker(user.getLocation(), user.getName(),
                    user.isLeader() ? MarkerColor.VIOLET : MarkerColor.CYAN);
        }
    }
}