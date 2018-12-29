package com.teal.a276.walkinggroup.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.map.EmbeddedCreateGroup;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MyGroups extends BaseActivity {
    private User user = new User();
    private List<Group> leadsGroups;
    private List<Group> memberOfGroups;

    public static Intent makeIntent(Context context) {
        return new Intent(context, MyGroups.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        user = ModelFacade.getInstance().getCurrentUser();

        callServerForUserList();
        setupCreateGroupButton();
    }

    private void callServerForUserList() {
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<User> call = proxy.getUserById(user.getId(), 1L);
        ServerManager.serverRequest(call, this::setInfo, this::error);
    }

    private void setupCreateGroupButton() {
        Button btn = findViewById(R.id.createGroupBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(EmbeddedCreateGroup.makeIntent(MyGroups.this));
                MyGroups.this.finish();
            }
        });
    }

    private void setInfo(User user) {
        memberOfGroups = user.getMemberOfGroups();
        leadsGroups = user.getLeadsGroups();

        List<String> leadsGroupNames = new ArrayList<>();
        List<String> memberOfGroupNames = new ArrayList<>();
        GroupManager groupManager = ModelFacade.getInstance().getGroupManager();
        List<Group> groups = groupManager.getGroups();

        setListViewNames(leadsGroupNames, memberOfGroupNames, groups);
        setArrayAdapters(leadsGroupNames, memberOfGroupNames);

        ListView leaderList = findViewById(R.id.leaderOfGroups);
        leaderList.setOnItemClickListener((parent, viewClicked, position, id) -> {
            Log.d("MyGroups", "Clicked  leads position " + position);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.group_message_alertdialog, null);
            String title = getString(R.string.msg_group, leadsGroupNames.get(position));
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setView(dialogView);

            alertDialogBuilder.setPositiveButton(getString(R.string.post), (dialog, which) -> {

                // Extract data from UI:
                EditText msg = dialogView.findViewById(R.id.messageEditText);
                String messageString = msg.getText().toString();

                if (!messageString.isEmpty()) {
                    Message message = new Message();
                    message.setText(messageString);

                    Group groupSelected = leadsGroups.get(position);

                    // Send message
                    ServerProxy proxy = ServerManager.getServerProxy();
                    Call<Message> call = proxy.sendMessageToGroup(groupSelected.getId(), message);
                    ServerManager.serverRequest(call, null, MyGroups.this::error);
                }
            });
            alertDialogBuilder.setNeutralButton(getString(R.string.Emergency), (dialog, which) -> {

                // Extract data from UI:
                EditText msg = dialogView.findViewById(R.id.messageEditText);
                String messageString = getString(R.string.emerg_txt) + (msg.getText().toString());

                if (!messageString.isEmpty()) {
                    Message message = new Message();
                    message.setText(messageString);
                    Group groupSelected = leadsGroups.get(position);
                    ServerProxy proxy = ServerManager.getServerProxy();
                    Call<Message> call = proxy.sendMessageToGroup(groupSelected.getId(), message);
                    ServerManager.serverRequest(call, null, MyGroups.this::error);
                }
            });

            alertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);

            alertDialogBuilder.show();
        });

        ListView membList = findViewById(R.id.memberOfGroups);
        membList.setOnItemClickListener((parent, viewClicked, position, id) -> {
            Log.d("MyGroups", "Clicked  member position " + position);
            // Launch group info
            makeIntent(memberOfGroupNames, position);
        });
    }

    private void setListViewNames(List<String> leadsGroupNames, List<String> memberOfGroupNames, List<Group> groups) {
        for(int i = 0; i < groups.size(); i++){
            Group leadsGroup = groups.get(i);
            for (int j = 0; j < leadsGroups.size(); j++){
                if(leadsGroup.getId().equals(leadsGroups.get(j).getId())) {
                    leadsGroupNames.add(groups.get(i).getGroupDescription());
                }
            }
        }

        for(int i = 0; i < groups.size(); i++){
            Group memberGroup = groups.get(i);
            for (int j = 0; j < memberOfGroups.size(); j++){
                if(memberGroup.getId().equals(memberOfGroups.get(j).getId())) {
                    memberOfGroupNames.add(groups.get(i).getGroupDescription());
                }
            }
        }
    }

    private void setArrayAdapters(List<String> leadsGroupNames, List<String> memberOfGroupNames) {
        ArrayAdapter<String> groupsILeadAdapter = new ListItemAdapter(this, leadsGroupNames);
        ListView groupsILead = findViewById(R.id.leaderOfGroups);
        groupsILead.setAdapter(groupsILeadAdapter);

        ArrayAdapter<String> groupsImInAdapter = new ListItemAdapter(this, memberOfGroupNames);
        ListView groupsImIn = findViewById(R.id.memberOfGroups);
        groupsImIn.setAdapter(groupsImInAdapter);
    }

    private void makeIntent(List<String> leadsGroupNames, int position) {
        Group group = new Group();
        group.setGroupDescription(leadsGroupNames.get(position));
        Intent intent = GroupMembersInfo.makeIntent(MyGroups.this, group);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private class ListItemAdapter extends ArrayAdapter<String> {
        private final List<String> listItems;
        private final Context context;

        private ListItemAdapter(Context context, List<String> listItems) {
            super(context, R.layout.group_info_list_item, listItems);
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.my_groups_list_item, parent, false);
            }

            String item = listItems.get(position);
            TextView groupNameTextView = itemView.findViewById(R.id.groupName);
            groupNameTextView.setText(item);

            return itemView;
        }
    }
}



