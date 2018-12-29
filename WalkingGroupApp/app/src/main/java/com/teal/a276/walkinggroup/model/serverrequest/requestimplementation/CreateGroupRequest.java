package com.teal.a276.walkinggroup.model.serverrequest.requestimplementation;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.AbstractServerRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class CreateGroupRequest extends AbstractServerRequest {

    private final String leaderEmail;
    private final String groupDes;
    private final LatLng meetingLatLng;
    private final LatLng destLatLng;

    public CreateGroupRequest(String leaderEmail, String groupDes, LatLng meetingLatLng, LatLng destLatLng, @NonNull ServerError errorCallback) {
        super(null, errorCallback);
        this.leaderEmail = leaderEmail;
        this.groupDes = groupDes;
        this.meetingLatLng = meetingLatLng;
        this.destLatLng = destLatLng;
    }

    @Override
    public void makeServerRequest() {
        getUserForEmail(leaderEmail, this::userFromEmail, null);

    }

    private void userFromEmail(User user) {
        Group group = new Group();
        group.setLeader(user);

        group.setGroupDescription(groupDes);

        List<Double> latArray = new ArrayList<>();
        List<Double> lngArray = new ArrayList<>();

        latArray.add(meetingLatLng.latitude);
        latArray.add(destLatLng.latitude);
        lngArray.add(meetingLatLng.longitude);
        lngArray.add(destLatLng.longitude);

        group.setRouteLatArray(latArray);
        group.setRouteLngArray(lngArray);

        ServerProxy proxy = ServerManager.getServerProxy();
        Call<Group> call = proxy.createGroup(group);
        ServerManager.serverRequest(call, this::groupResult, errorCallback);
    }

    private void groupResult(Group group){
        setDataChanged(group);
    }
}
