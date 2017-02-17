package com.breezee.sergeystasyuk.weatherinfo;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by User on 15.02.2017.
 */

public class TrackLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public interface OnLocationTrackedListener {
        void onLocationTracked(Location location);
    }

    private OnLocationTrackedListener listener;
    private Context context;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    private int REQUEST_CHECK_SETTINGS  = 1000;

    public TrackLocation(Context context, OnLocationTrackedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /*
     * GoogleApiClient initialising and LocationRequest building.
     * */
    public void buildRequest(){
        if (checkPlayServices(context)) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            checkForLocationReuqestSetting(locationRequest);

            googleApiClient.connect();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        //we are connected to a network
        return netInfo != null && netInfo.isConnected();
    }

    /*
    * Check if google play services are installed on the device.
    * */
    private boolean checkPlayServices(Context context) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode))
                api.getErrorDialog(((Activity) context), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else {
                Toast.makeText(context, "This device is not supported.", Toast.LENGTH_LONG).show();
                ((Activity) context).finish();
            }
            return false;
        }
        return true;
    }

    /*
    * Stop retrieving locations when we go out of the application.
    * */
    public void stopLocationUpdates() {
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    /*
   * Enable retrieving locations.
   * */
    public void enableLocationUpdates() {
        if (googleApiClient != null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
    * Callback method of GoogleApiClient.ConnectionCallbacks
    * */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            enableLocationUpdates();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }

    }

    /*
    * Callback method of GoogleApiClient.ConnectionCallbacks
    * */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("MainActivity", "onConnectionSuspended called...");
    }

    /*
    * Callback method of GoogleApiClient.OnConnectionFailedListener
    * */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("MainActivity", "onConnectionFailed called.");
    }

    /*
   * Callback method of Locationlistener.
   * */
    @Override
    public void onLocationChanged(final Location location) {
        if (googleApiClient != null) {
            //Once location is defined we are disabling location updates
            stopLocationUpdates();
            googleApiClient.disconnect();
        }
        if (location != null) {
            listener.onLocationTracked(location);
        }
        else Toast.makeText(context, R.string.track_location_not_found, Toast.LENGTH_LONG).show();


    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (String per : permissions) {
            Log.d("MainActivity", "permissions are  " + per);
        }
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission loaded...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"Permission Denied, You cannot access location data.",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * Google Fused API require Runtime permission. Runtime permission is available for Android Marshmallow
     * or Greater versions.
     * @param locationRequest needed to check whether we need to prompt settings alert.
     */
    private void checkForLocationReuqestSetting(LocationRequest locationRequest) {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings
                    (googleApiClient, builder.build());
            result.setResultCallback(locationSettingsResult -> {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("MainActivity", "onResult: SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d("MainActivity", "onResult: RESOLUTION_REQUIRED");
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    (Activity)context,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("MainActivity", "onResult: SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
