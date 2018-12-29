package com.teal.a276.walkinggroup.activities.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;

/**
 * Class to create new groups, allowing users to select meeting/dest on mapView
 */

public class EmbeddedCreateGroup extends AbstractMapActivity {

    private LatLng meetingLocation;
    private LatLng destinationLocation;
    private Marker meetingMarker;
    private Marker destinationMarker;
    private final MarkerColor meetingColor = MarkerColor.CYAN;
    private final MarkerColor destinationColor = MarkerColor.RED;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embedded_create_group);
        initializeMap(R.id.embeddedMap, 0L, 0L);

        setupSelectDestinationButton();
        setupCreateButton();
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, EmbeddedCreateGroup.class);
    }

    private void setupSelectDestinationButton(){
        Button btn = findViewById(R.id.selectDestButton);
        btn.setOnClickListener(v -> {
            isClicked = !isClicked;
            int toastMessageId = isClicked ? R.string.leader_destination : R.string.user_destination;
            int buttonTextId = isClicked ? R.string.embedded_set_meeting : R.string.embedded_set_dest;

            Toast.makeText(EmbeddedCreateGroup.this, toastMessageId, Toast.LENGTH_SHORT).show();
            btn.setText(buttonTextId);
           if (!isClicked) {
                setMeetingCoordinates();
                return;
            }

            map.setOnMapClickListener(latLng -> {
                destinationMarker.remove();
                destinationMarker = replaceLocationMarker(latLng, R.string.embedded_destination, destinationColor);
                destinationMarker.setVisible(true);
                destinationLocation = latLng;
            });
        });
    }

    private void setupCreateButton() {
        Button btn = findViewById(R.id.embeddedCreateButton);
        btn.setOnClickListener(v ->{
            EditText nameVal = findViewById(R.id.embeddedNameEdit);
            String nameValStr = nameVal.getText().toString();

            EditText leadersEmailVal = findViewById(R.id.embeddedEmailEdit);
            String leadersEmailStr = leadersEmailVal.getText().toString();

            if(nameValStr.isEmpty()) {
                nameVal.setError(getString(R.string.empty_group_name));
                return;
            }
            if(!User.validateEmail(leadersEmailStr)) {
                leadersEmailVal.setError(getString(R.string.invalid_email));
                return;
            }
            if((meetingLocation.latitude == 0) && (meetingLocation.longitude == 0)) {
                Toast.makeText(
                        EmbeddedCreateGroup.this,
                        R.string.embedded_location_not_set,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("Coords", "meetingLat/Lng: " + meetingLocation.latitude + "/"
                    + meetingLocation.longitude + " destLat/Lng:" + destinationLocation.latitude
                    + "/" + destinationLocation.longitude);

            ModelFacade.getInstance().getGroupManager().addNewGroup(
                    leadersEmailStr,
                    nameValStr,
                    meetingLocation,
                    destinationLocation,
                    EmbeddedCreateGroup.this::error);

            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;
        setMeetingCoordinates();
    }

    private void setMeetingCoordinates(){
        map.setOnMapClickListener(latLng -> {
            meetingMarker.remove();
            meetingMarker = replaceLocationMarker(latLng, R.string.embedded_set_meeting, meetingColor);
            meetingLocation = latLng;
        });
    }

    private Marker replaceLocationMarker(LatLng location, int titleId, MarkerColor color) {
        map.animateCamera(CameraUpdateFactory.newLatLng(location));
        Marker marker = placeMarker(location, getString(titleId), color);
        marker.showInfoWindow();

        Log.d("Marker placed at ", "Lat: " + location.latitude + "Long: " + location.longitude);
        return marker;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        placeCurrentLocationMarker();
        addInitialMarkers();
    }

    private void addInitialMarkers() {
        meetingLocation = locationToLatLng(lastLocation);
        meetingMarker = placeMarker(meetingLocation, getString(R.string.meeting), meetingColor);

        destinationMarker = placeMarker(meetingLocation, getString(R.string.destination), destinationColor);
        destinationMarker.setVisible(false);

        Log.d("initial location", "Lat" + meetingLocation.latitude + "Lng" + meetingLocation.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}