package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;

/**
 * Activity for displaying the top 100 users starting with the highest score
 */

public class Leaderboard extends BaseActivity {

    public static final int LEADERBOARD_MAX_USERS = 100;
    private ArrayAdapter<User> leaderboardAdapter;
    String firstName= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        getUsers();
    }

    private void getUsers() {
        ServerProxy getUsersProxy = ServerManager.getServerProxy();
        Call<List<User>> getUsersCall = getUsersProxy.getUsers();
        ServerManager.serverRequest(getUsersCall, this::initializeLeaderboardListView, this::error);
    }

    private void initializeLeaderboardListView(List<User> allUsers) {

        Log.d("listtesting", Arrays.deepToString(allUsers.toArray()));

        for(User user : allUsers){
            if(user.getTotalPointsEarned() == null){
                user.setTotalPointsEarned(0);
            }
        }

        Collections.sort(allUsers, ((o1, o2) -> o2.getTotalPointsEarned()
                .compareTo(o1.getTotalPointsEarned())));

        if(allUsers.size()> LEADERBOARD_MAX_USERS){
            allUsers.subList(LEADERBOARD_MAX_USERS, allUsers.size());
        }

        leaderboardAdapter = new ListItemAdapter(this, allUsers);
        ListView leaderboardList = findViewById(R.id.leaderboardLv);
        leaderboardList.setAdapter(leaderboardAdapter);
    }

    private class ListItemAdapter extends ArrayAdapter<User> {
        private final List<User> listItems;
        private final Context context;

        public ListItemAdapter(Context context, List<User> listItems) {

            super(context, R.layout.list_item, listItems);
            this.listItems = listItems;
            this.context = context;
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.leaderboard_list, parent, false);
            }
            User selectedUser = listItems.get(position);
            TextView nameTextView = itemView.findViewById(R.id.userName);
            String currentName = selectedUser.getName();

            firstName = getFirstName(currentName);
            char lastInitial = getLastInitial(currentName);

            String displayText =  String.format(getString(R.string.leaderboard_display_points),
                    firstName, lastInitial, selectedUser.getTotalPointsEarned());
            nameTextView.setText(displayText);
            return itemView;
        }
    }

    public String getFirstName(String currentName){
        if(currentName.split("\\w+").length>1){
            firstName = currentName.substring(0, currentName.lastIndexOf(' '));
        } else {
            firstName = currentName;
        }
        return firstName;
    }

    public char getLastInitial(String currentName){
        char lastInitial = '\0';
        for(int i=1;i<currentName.length();i++){
            char c = currentName.charAt(i);
            if(c == ' '){
                lastInitial = currentName.charAt(i+1);
            }
        }
        return lastInitial;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, Leaderboard.class);
    }
}
