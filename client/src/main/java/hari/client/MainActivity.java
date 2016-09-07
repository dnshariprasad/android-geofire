package hari.client;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        (new Task()).execute();
    }

    class Task extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locs");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation("firebase-hq", new GeoLocation(37.7853889, -122.4056973));
            return null;
        }
    }
}
