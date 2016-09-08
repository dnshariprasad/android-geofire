package hari.client;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISSION_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (PermissionUtil.checkLocationPermission(this)) {
            LocationService.start(this);
        } else {
            ActivityCompat.requestPermissions(this, PermissionUtil.PERMISSIONS_LOCATION, REQUEST_PERMISSION_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Thank you! Enjoy our services..", Toast.LENGTH_SHORT).show();
                    LocationService.start(this);
                } else {
                    Toast.makeText(MainActivity.this, "Sorry! We can not serve you better if you do not allow to access your location ..", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
