package hari.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GeoQueryEventListener geoQueryEventListener = new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(String key, GeoLocation location) {
            System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
        }

        @Override
        public void onKeyExited(String key) {
            System.out.println(String.format("Key %s is no longer in the search area", key));
        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {
            System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));

            LatLng loc = new LatLng(location.latitude, location.longitude);

            if (mMap != null) {
                removeMarker(key);
                Marker marker = mMap.addMarker(new MarkerOptions().position(loc).title(key).draggable(true));
                markers.put(key, marker);
            }
        }

        @Override
        public void onGeoQueryReady() {
            System.out.println("All initial data has been loaded and events have been fired!");
        }

        @Override
        public void onGeoQueryError(DatabaseError error) {
            System.err.println("There was an error with this query: " + error);
        }
    };
    private GoogleMap mMap;
    private GeoFire geoFire;
    private DatabaseReference ref;
    private GeoQuery geoQuery;
    private HashMap<String, Marker> markers;

    public static void start(Context context) {
        Intent starter = new Intent(context, MapActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markers = new HashMap<>();

        ref = FirebaseDatabase.getInstance().getReference("patients_locations/" + "3849132c-b517-e611-80cb-22000b0a8c46");
        geoFire = new GeoFire(ref);

        geoQuery = geoFire.queryAtLocation(new GeoLocation(17.444716, 78.396750), 1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        geoQuery.addGeoQueryEventListener(geoQueryEventListener);
        mMap = googleMap;
    }

    private void removeMarker(String title) {
        if (markers.get(title) != null) {
            markers.get(title).remove();
            markers.remove(title);
        }
    }
}
