package com.teal.a276.walkinggroup.activities.permission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Authorizor;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Permission;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.PermissionStatus;

import java.util.List;
import java.util.Map;

/**
 * 2d list the contains display text for a permission request as a header
 * and all users who need to respond to the request as children
 */

public class PermissionAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Permission> headerData;
    private Map<Permission, List<Authorizor>> childData;
    private SetPermission acceptPermission;
    private SetPermission declinePermission;

    interface SetPermission {
        void setPermissionStatus(Long id, PermissionStatus status);
    }


    PermissionAdapter(Context context,
                      List<Permission> headerData, Map<Permission, List<Authorizor>> childData,
                      SetPermission acceptPermission, SetPermission declinePermission) {
        this.context = context;
        this.headerData = headerData;
        this.childData = childData;
        this.acceptPermission = acceptPermission;
        this.declinePermission = declinePermission;
    }

    @Override
    public int getGroupCount() {
        return headerData.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return getChildList(i).size();
    }

    @Override
    public Permission getGroup(int i) {
        return headerData.get(i);
    }

    @Override
    public Authorizor getChild(int headerIndex, int childIndex) {
        return getChildList(headerIndex).get(childIndex);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int headerIndex, boolean b, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.permission_list_item_header, null);
        }

        TextView textView = view.findViewById(R.id.headerTitle);
        Permission permission = getGroup(headerIndex);
        textView.setText(String.format(context.getString(R.string.lead_group_request),
                permission.getUserA().getName(), permission.getGroupG().getGroupDescription()));

        return view;
    }

    @Override
    public View getChildView(int headerIndex, int childIndex, boolean b, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.permission_list_item, null);
        }

        Permission permission = getGroup(headerIndex);
        Authorizor authorizor = getChild(headerIndex, childIndex);
        User currentUser = ModelFacade.getInstance().getCurrentUser();
        TextView status = view.findViewById(R.id.permissionStatus);

        if(isPendingAuthorizor(authorizor, currentUser, permission)) {
            ImageView accept = view.findViewById(R.id.acceptPermission);
            accept.setVisibility(View.VISIBLE);
            if (acceptPermission != null) {
                accept.setOnClickListener(view1 ->
                        acceptPermission.setPermissionStatus(permission.getId(), PermissionStatus.APPROVED)
                );
            }

            ImageView decline = view.findViewById(R.id.declinePermission);
            decline.setVisibility(View.VISIBLE);
            if (declinePermission != null) {
                decline.setOnClickListener(view1 ->
                        declinePermission.setPermissionStatus(permission.getId(), PermissionStatus.DECLINED)
                );
            }

            status.setVisibility(View.INVISIBLE);
        } else {
            status.setText(authorizor.getStatus());
        }

        TextView textView = view.findViewById(R.id.name);
        textView.setText(authorizor.getUsers().get(0).getName());

        return view;
    }

    private boolean isPendingAuthorizor(Authorizor authorizor, User currentUser, Permission permission) {
        User authUser = authorizor.getUsers().get(0);
        String pending = PermissionStatus.PENDING.getValue();
        return permission.getStatus().equals(pending) &&
                authUser.equals(currentUser) && authorizor.getStatus().equals(pending);
    }

    void removePermission(Permission result) {
       headerData.remove(result);
       childData.remove(result);
       notifyDataSetChanged();
    }

    void addPermission(Permission permission) {
        headerData.add(permission);
        childData.put(permission, permission.getAuthorizors());
        notifyDataSetChanged();
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private List<Authorizor> getChildList(int index) {
        Permission headerItem = headerData.get(index);
        return childData.get(headerItem);
    }
}
