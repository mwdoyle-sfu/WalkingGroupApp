package com.teal.a276.walkinggroup.activities.map;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;
import com.teal.a276.walkinggroup.model.dataobjects.UserLocation;

import java.io.IOException;
import java.util.List;

/**
 * Abstract class to store the shared logic between all map activities
 */

@SuppressWarnings("deprecation")
public abstract class AbstractMapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    protected enum MarkerColor {
        AZURE(BitmapDescriptorFactory.HUE_AZURE),
        VIOLET(BitmapDescriptorFactory.HUE_VIOLET),
        CYAN(BitmapDescriptorFactory.HUE_CYAN),
        GREEN(BitmapDescriptorFactory.HUE_GREEN),
        RED(BitmapDescriptorFactory.HUE_RED);

        private final float colorValue;
        MarkerColor(float colorValue) {
            this.colorValue = colorValue;
        }

        public float getColorValue() {
            return colorValue;
        }
    }

    private static final int MAX_RESULTS = 1;
    private final int ZOOM_LEVEL = 10;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final int REQUEST_CHECK_SETTINGS = 2;

    protected GoogleMap map;
    protected GoogleApiClient googleApiClient;
    protected Location lastLocation;
    protected boolean updateLocation;

    private LocationRequest locationRequest;

    protected void initializeMap(int fragmentId, Long interval, Long fastestInterval) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(fragmentId);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        createLocationRequest(interval, fastestInterval);
    }

    // Handles any changes to be made based on the current state of the user’s location settings
    protected void createLocationRequest(Long interval, Long fastestInterval) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdates();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.getStackTraceString(e);
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }

    void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected() && !updateLocation) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        if(updateLocation) {
            startLocationUpdates();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Map Connection Error", connectionResult.getErrorMessage() +
                "\nError code: " + connectionResult.getErrorCode());
        this.error(connectionResult.getErrorMessage());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private boolean checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @SuppressLint("MissingPermission")
    protected void setUpMap() {
        if (!checkAndRequestPermissions()) {
            return;
        }

        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @SuppressLint("MissingPermission")
    void placeCurrentLocationMarker() {
        if(!checkAndRequestPermissions()) {
            return;
        }

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                LatLng currentLocation = locationToLatLng(lastLocation);
                placeMarker(currentLocation, getAddress(currentLocation), R.mipmap.ic_user_location);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL));
            }
        }
    }

    protected Marker placeMarker(LatLng markerLocation, String markerTitle, Integer iconId) {
        MarkerOptions markerOptions = new MarkerOptions().position(markerLocation).title(markerTitle);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
                    (getResources(), iconId)));

        return map.addMarker(markerOptions);
    }

    protected Marker placeMarker(LatLng markerLocation, String markerTitle, MarkerColor markerColor) {
        MarkerOptions markerOptions = new MarkerOptions().position(markerLocation).title(markerTitle);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerColor.getColorValue()));

        return map.addMarker(markerOptions);
    }


    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        StringBuilder addressText = new StringBuilder();
        List<Address> addresses;
        Address address;

        if (!isValidLatLng(latLng.latitude, latLng.longitude)) {
            addressText = addressText.append(getString(R.string.address_error));
            return addressText.toString();
        }

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, MAX_RESULTS);
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                addressText = addressText.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.getStackTraceString(e);
        }

        return addressText.toString();
    }

    // Source: https://stackoverflow.com/questions/7356373/android-how-to-validate-locations-latitude-and-longtitude-values
    private boolean isValidLatLng(double lat, double lng) {
        return lat < -90 || lat > 90 || lng < -180 || lng > 180;
    }

    @NonNull
    protected LatLng locationToLatLng(Location location) {
        if (location == null) {
            return new LatLng(0, 0);
        }

        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @NonNull
    protected LatLng locationToLatLng(UserLocation location) {
        if (location == null) {
            return new LatLng(0, 0);
        }

        return new LatLng(location.getLat(), location.getLng());
    }
}
