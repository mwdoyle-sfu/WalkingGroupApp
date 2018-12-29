package com.teal.a276.walkinggroup.activities.permission;

import android.os.Bundle;
import android.widget.ExpandableListView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Authorizor;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Permission;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.PermissionStatus;
import com.teal.a276.walkinggroup.model.serverproxy.RequestConstant;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * General idea taken from here https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial
 * Activity that displays currently active permission request relevant to the user, and all previously processed requests
 */
public class PermissionRequest extends BaseActivity {
    PermissionAdapter activePermissionAdapter;
    PermissionAdapter previousPermissionAdapter;

    ExpandableListView activePermissionView;
    ExpandableListView previousPermissionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_request);

        activePermissionView = findViewById(R.id.activePermissions);
        previousPermissionView = findViewById(R.id.previousPermissions);
        requestPermission();
    }

    private void requestPermission() {
        ServerProxy proxy = ServerManager.getServerProxy();
        Map<String, Object> map = new HashMap<>();
        map.put(RequestConstant.USER_ID, ModelFacade.getInstance().getCurrentUser().getId());
        Call<List<Permission>> previousPermissionsRequest = proxy.getPermissions(map, 1L);
        ServerManager.serverRequest(previousPermissionsRequest, this::previousPermissionsResult, this::error);

        map.put(RequestConstant.STATUS, PermissionStatus.PENDING);
        Call<List<Permission>> activePermissionsRequest = proxy.getPermissions(map, 1L);
        ServerManager.serverRequest(activePermissionsRequest, this::activePermissionsResult, this::error);
    }

    private void activePermissionsResult(List<Permission> permissions) {
        activePermissionAdapter = new PermissionAdapter(this, permissions,
                getAuthorizors(permissions), this::setPermissionStatus, this::setPermissionStatus);
        activePermissionView.setAdapter(activePermissionAdapter);
        expandList(activePermissionView, activePermissionAdapter.getGroupCount());
    }

    private void setPermissionStatus(Long id, PermissionStatus status) {
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<Permission> call = proxy.setPermissionStatus(id, status.getValue(), 1L);
        ServerManager.serverRequest(call, this::permissionStatusChangeResult, this::error);
    }

    private void permissionStatusChangeResult(Permission permission) {
        activePermissionAdapter.removePermission(permission);
        previousPermissionAdapter.addPermission(permission);
        expandList(previousPermissionView, previousPermissionAdapter.getGroupCount());
    }

    private void previousPermissionsResult(List<Permission> permissions) {
        List<Permission> previousPermissions = getPreviousPermissions(permissions);
        previousPermissionAdapter = new PermissionAdapter(this, previousPermissions,
                getAuthorizors(previousPermissions), null, null);
        previousPermissionView.setAdapter(previousPermissionAdapter);
        expandList(previousPermissionView, previousPermissionAdapter.getGroupCount());
    }

    private void expandList(ExpandableListView view, int groupSize) {
        for (int i = 0; i < groupSize; i++) {
            view.expandGroup(i);
        }
    }

    private List<Permission> getPreviousPermissions(List<Permission> permissions) {
        List<Permission> previousPermissions = new ArrayList<>();
        for(Permission p : permissions) {
            if(!p.getStatus().equals(PermissionStatus.PENDING.getValue())) {
                previousPermissions.add(p);
            }
        }

        return previousPermissions;
    }

    private Map<Permission, List<Authorizor>> getAuthorizors(List<Permission> permissions) {
        HashMap<Permission, List<Authorizor>> authorizors = new HashMap<>();
        for(Permission p : permissions) {
                authorizors.put(p, p.getAuthorizors());
        }

        return authorizors;
    }
}
