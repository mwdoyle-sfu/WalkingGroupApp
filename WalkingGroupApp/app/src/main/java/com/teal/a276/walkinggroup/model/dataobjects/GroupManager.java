package com.teal.a276.walkinggroup.model.dataobjects;

import com.google.android.gms.maps.model.LatLng;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.CreateGroupRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Class to interact with the server proxy and allow for notification of group creation data
 */

public class GroupManager extends Observable {
    private List<Group> groups = new ArrayList<>();

    public void addNewGroup(String leaderEmail, String groupDescription, LatLng meeting, LatLng dest, ServerError errorCallback) {
        CreateGroupRequest request = new CreateGroupRequest(leaderEmail, groupDescription, meeting, dest, errorCallback);
        request.makeServerRequest();
        request.addObserver((observable, o) -> {
            groups.add((Group)o);
            setChanged();
            notifyObservers(o);
        });
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
