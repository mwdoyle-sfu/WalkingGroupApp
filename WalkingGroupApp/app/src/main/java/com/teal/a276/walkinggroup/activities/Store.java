package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.map.MapsActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.UnlockedRewards;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 *  Displays Store interface for user to apply themes and backgrounds
 */
public class Store extends BaseActivity implements View.OnClickListener {
    public static final int ITEM_PRICE = 100;

    private User user;
    private Integer remainingPoints;
    private ImageView itemOne;
    private ImageView itemTwo;
    private ImageView itemThree;
    private ImageView itemFour;
    private ImageView itemFive;
    private ImageView itemSix;
    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;
    private Button btnFive;
    private Button btnSix;
    List<ImageView> allItems = new ArrayList<>();
    String retrievedJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        user = ModelFacade.getInstance().getCurrentUser();

        ServerProxy proxy = ServerManager.getServerProxy();
        Call<User> result = proxy.getUserById(user.getId(), 1L);
        ServerManager.serverRequest(result, this::updateUser, this::error);

        setupAllElements();
        updateRemainingPoints();
        updateAvailableItems();
        updateItemClickListeners();
        setupPurchaseButtonClickListener();
        setupDefaultBtn();
        setupApplyBtn();
    }

    public void updateUser(User updatedUser){
        user = updatedUser;
        updateRemainingPoints();
        updateAvailableItems();
    }

    private void setupAllElements() {
        itemOne = findViewById(R.id.one);
        itemOne.setEnabled(false);
        itemTwo = findViewById(R.id.two);
        itemTwo.setEnabled(false);
        itemThree = findViewById(R.id.three);
        itemThree.setEnabled(false);
        itemFour = findViewById(R.id.four);
        itemFour.setEnabled(false);
        itemFive = findViewById(R.id.five);
        itemFive.setEnabled(false);
        itemSix = findViewById(R.id.six);
        itemSix.setEnabled(false);

        allItems.add(itemOne);
        allItems.add(itemTwo);
        allItems.add(itemThree);
        allItems.add(itemFour);
        allItems.add(itemFive);
        allItems.add(itemSix);

        btnOne = findViewById(R.id.button1);
        btnTwo = findViewById(R.id.button2);
        btnThree = findViewById(R.id.button3);
        btnFour= findViewById(R.id.button4);
        btnFive = findViewById(R.id.button5);
        btnSix = findViewById(R.id.button6);
    }

    private void updateAvailableItems(){
        retrievedJson = user.getCustomJson();
        UnlockedRewards retrievedRewards = new UnlockedRewards();
        retrievedRewards = retrievedJsonCheck(retrievedRewards);
        List<Integer> retrieved = retrievedRewards.getUnlockedItems();

        if(retrieved.contains(1)){
            itemOne.setEnabled(true);
            btnOne.setVisibility(View.GONE);
        }
        if(retrieved.contains(2)){
            itemTwo.setEnabled(true);
            btnTwo.setVisibility(View.GONE);
        }
        if(retrieved.contains(3)){
            itemThree.setEnabled(true);
            btnThree.setVisibility(View.GONE);
        }
        if(retrieved.contains(4)){
            itemFour.setEnabled(true);
            btnFour.setVisibility(View.GONE);
        }
        if(retrieved.contains(5)){
            itemFive.setEnabled(true);
            btnFive.setVisibility(View.GONE);
        }
        if(retrieved.contains(6)){
            itemSix.setEnabled(true);
            btnSix.setVisibility(View.GONE);
        }

    }


    private void setupDefaultBtn() {
        Button button = findViewById(R.id.storeDefaultBtn);
        button.setOnClickListener((View v) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt(CURRENT_THEME, R.style.AppTheme);
            editor.apply();

            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    private void setupApplyBtn() {
        Button button = findViewById(R.id.storeApplyBtn);
        button.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void updateRemainingPoints() {
        TextView tv = findViewById(R.id.storeRemainingPointsTv);
        remainingPoints = user.getCurrentPoints();
        String remainingPointsString = getString(R.string.store_remaining_points) + remainingPoints;
        tv.setText(remainingPointsString);
    }

    public void updateItemClickListeners(){
        for(ImageView imageView : allItems){
            if(imageView.isEnabled()){
                imageView.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        switch(v.getId()){
            case R.id.one:
                editor.putInt(CURRENT_THEME, getNewTheme("boxes", null));
                displayToast("Boxes");
                editor.apply();
                break;
            case R.id.two:
                editor.putInt(CURRENT_THEME, getNewTheme("circle", null));
                displayToast("Circle");
                editor.apply();
                break;
            case R.id.three:
                editor.putInt(CURRENT_THEME, getNewTheme("wave", null));
                displayToast("Wave");
                editor.apply();
                break;
            case R.id.four:
                editor.putInt(CURRENT_THEME, getNewTheme(null, "blue"));
                displayToast("Blue");
                editor.apply();
                break;
            case R.id.five:
                editor.putInt(CURRENT_THEME, getNewTheme(null, "green"));
                displayToast("Green");
                editor.apply();
                break;
            case R.id.six:
                editor.putInt(CURRENT_THEME, getNewTheme(null, "purple"));
                displayToast("Purple");
                editor.apply();
                break;
        }
    }

    public void displayToast(String text){
        Toast.makeText(this, "You have selected: " + text, Toast.LENGTH_SHORT).show();
    }

    private int getNewTheme(String newBackground, String newColor) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currTheme = preferences.getInt(CURRENT_THEME, -1);
        String type = "default";
        String color = "default";

        if (currTheme == R.style.AppTheme || currTheme == R.style.AppTheme_box || currTheme == R.style.AppTheme_Dark_Green_Box ||
                currTheme == R.style.AppTheme_Dark_Purple_Box || currTheme == R.style.AppTheme_Light_Blue_Box) {
            type = "boxes";
        } else if (currTheme == R.style.AppTheme_circle || currTheme == R.style.AppTheme_Dark_Green_Circle ||
                currTheme == R.style.AppTheme_Dark_Purple_Circle || currTheme == R.style.AppTheme_Light_Blue_Circle) {
            type = "circle";
        } else if (currTheme == R.style.AppTheme_wave || currTheme == R.style.AppTheme_Dark_Green_Wave ||
                currTheme == R.style.AppTheme_Dark_Purple_Wave || currTheme == R.style.AppTheme_Light_Blue_Wave) {
            type = "wave";
        }

        if (currTheme == R.style.AppTheme_Light_Blue_Box || currTheme == R.style.AppTheme_Light_Blue_Circle ||
                currTheme == R.style.AppTheme_Light_Blue_Wave) {
            color = "blue";
        } else if (currTheme == R.style.AppTheme_Dark_Purple_Box || currTheme == R.style.AppTheme_Dark_Purple_Circle ||
                currTheme == R.style.AppTheme_Dark_Purple_Wave) {
            color = "purple";
        } else if (currTheme == R.style.AppTheme_Dark_Green_Box || currTheme == R.style.AppTheme_Dark_Green_Circle ||
                currTheme == R.style.AppTheme_Dark_Green_Wave) {
            color = "green";
        }

       //dealing with new background
        if (newColor == null) {
            switch (newBackground) {
                case "boxes":
                    switch (color) {
                        case "blue":
                            return R.style.AppTheme_Light_Blue_Box;
                        case "purple":
                            return R.style.AppTheme_Dark_Purple_Box;

                        case "green":
                            return R.style.AppTheme_Dark_Green_Box;

                        default:
                            return R.style.AppTheme_box;
                    }
                case "circle":
                    switch (color) {
                        case "blue":
                            return R.style.AppTheme_Light_Blue_Circle;
                        case "purple":
                            return R.style.AppTheme_Dark_Purple_Circle;

                        case "green":
                            return R.style.AppTheme_Dark_Green_Circle;

                        default:
                            return R.style.AppTheme_circle;
                    }
                case "wave":
                    switch (color) {
                        case "blue":
                            return R.style.AppTheme_Light_Blue_Wave;
                        case "purple":
                            return R.style.AppTheme_Dark_Purple_Wave;
                        case "green":
                            return R.style.AppTheme_Dark_Green_Wave;

                        default:
                            return R.style.AppTheme_wave;
                    }
            }
        }

        //dealing with new color
        if (newBackground == null) {
            switch (newColor) {
                case "blue":
                    switch (type) {
                        case "boxes":
                            return R.style.AppTheme_Light_Blue_Box;
                        case "wave":
                            return R.style.AppTheme_Light_Blue_Wave;

                        case "circle":
                            return R.style.AppTheme_Light_Blue_Circle;

                        default:
                            return R.style.AppTheme_Light_Blue_Box;
                    }
                case "purple":
                    switch (type) {
                        case "boxes":
                            return R.style.AppTheme_Dark_Purple_Box;
                        case "wave":
                            return R.style.AppTheme_Dark_Purple_Wave;

                        case "circle":
                            return R.style.AppTheme_Dark_Purple_Circle;

                        default:
                            return R.style.AppTheme_Dark_Purple_Box;
                    }
                case "green":
                    switch (type) {
                        case "boxes":
                            return R.style.AppTheme_Dark_Green_Box;
                        case "wave":
                            return R.style.AppTheme_Dark_Green_Wave;
                        case "circle":
                            return R.style.AppTheme_Dark_Green_Circle;

                        default:
                            return R.style.AppTheme_Dark_Green_Box;
                    }
                case "default":
                    switch(type) {
                        case "boxes":
                            return R.style.AppTheme;
                        case "wave":
                            return R.style.AppTheme_wave;
                        case "circle":
                            return R.style.AppTheme_circle;
                    }
            }
        }
        return -1;

    }


    public void setupPurchaseButtonClickListener(){
        btnOne.setOnClickListener((View v) -> purchaseItem(1, btnOne, itemOne));
        btnTwo.setOnClickListener((View v) -> purchaseItem(2, btnTwo, itemTwo));
        btnThree.setOnClickListener((View v) -> purchaseItem(3, btnThree, itemThree));
        btnFour.setOnClickListener((View v) -> purchaseItem(4, btnFour, itemFour));
        btnFive.setOnClickListener((View v) -> purchaseItem(5, btnFive, itemFive));
        btnSix.setOnClickListener((View v) -> purchaseItem(6, btnSix, itemSix));
    }

    public void purchaseItem(int id, Button button, ImageView imageView) {
        if (remainingPoints >= ITEM_PRICE) {
            UnlockedRewards retrievedRewards = getRetrievedRewards();
            List<Integer> retrieved = retrievedRewards.getUnlockedItems();
            retrieved.add(id);
            sendJsonToServer(retrieved);
            purchaseSuccessful(button, imageView);
        } else {
            Toast.makeText(this, R.string.store_not_enough_points, Toast.LENGTH_SHORT).show();
        }
    }

    public UnlockedRewards getRetrievedRewards() {
        retrievedJson = user.getCustomJson();
        UnlockedRewards retrievedRewards = new UnlockedRewards();
        retrievedRewards = retrievedJsonCheck(retrievedRewards);
        return retrievedRewards;
    }

    public UnlockedRewards retrievedJsonCheck(UnlockedRewards retrievedRewards){
        if (retrievedJson != null) {
            try {
                retrievedRewards =
                        new ObjectMapper().readValue(
                                retrievedJson,
                                UnlockedRewards.class);
                Log.w("deserialize", "De-serialized embedded rewards object: " + retrievedJson);
                return retrievedRewards;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retrievedRewards;
    }

    public void sendJsonToServer(List<Integer> retrieved){
        UnlockedRewards reward = new UnlockedRewards();
        reward.setUnlockedItems(retrieved);
        String customJson = null;
        try {
            customJson = new ObjectMapper().writeValueAsString(reward);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (customJson != null) {
            user.setCustomJson(customJson);
            ServerProxy proxy = ServerManager.getServerProxy();
            Call<User> result = proxy.updateUser(user.getId(), user, 1L);
            ServerManager.serverRequest(result, null, this::error);
        }
    }
    public void purchaseSuccessful(Button button, ImageView imageView){
        user.setCurrentPoints(user.getCurrentPoints() - ITEM_PRICE);
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<User> caller = proxy.updateUser(user.getId(), user, 1L);
        ServerManager.serverRequest(caller, result -> updatePoints(user, button, imageView), this::error);
    }

    public void updatePoints(User user, Button button, ImageView imageView){
        ModelFacade.getInstance().setCurrentUser(user);
        updateRemainingPoints();
        updateItemClickListeners();
        Toast.makeText(this, R.string.store_purchase_successful, Toast.LENGTH_SHORT).show();
        button.setVisibility(View.GONE);
        imageView.setEnabled(true);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, Store.class);
    }
}
