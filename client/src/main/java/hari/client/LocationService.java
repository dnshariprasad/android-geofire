package hari.client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public LocationService() {
    }

    private static final String TAG = "LocationService";
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference ref;
    private GeoFire geoFire;
    private String name;
    public static void start(Context context) {
        Intent starter = new Intent(context, LocationService.class);
        context.startService(starter);
    }

    @Override
    public void onCreate() {
        // The service is being created
        super.onCreate();
        Log.d(TAG, "onCreate");
        name = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getString(Constant.NAME, "");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        ref = FirebaseDatabase.getInstance().getReference("patients_locations");
        geoFire = new GeoFire(ref);

        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "GoogleApiClient.ConnectionCallbacks-onConnected");
        if (PermissionUtil.checkLocationPermission(this)) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            geoFire.setLocation(name, new GeoLocation(location.getLatitude(), location.getLongitude()));
            getLocationUpdates();
        } else {
            Toast.makeText(LocationService.this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "getLocationUpdates: No location permission.");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (PermissionUtil.checkLocationPermission(this)) {
            geoFire.setLocation(name, new GeoLocation(location.getLatitude(), location.getLongitude()));
        } else {
            Toast.makeText(LocationService.this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "getLocationUpdates: No location permission.");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient.OnConnectionFailedListener-onConnectionSuspended");
        Toast.makeText(LocationService.this, "Location Connection Suspended.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, " GoogleApiClient.OnConnectionFailedListener - onConnectionSuspended");
        Toast.makeText(LocationService.this, "Location Connection Failed.", Toast.LENGTH_SHORT).show();
    }


    private void getLocationUpdates() {
        if (PermissionUtil.checkLocationPermission(this)) {
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(3000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Toast.makeText(LocationService.this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "getLocationUpdates: No location permission.");
        }
    }
}
