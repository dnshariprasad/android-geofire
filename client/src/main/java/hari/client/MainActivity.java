package hari.client;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public static final int REQUEST_PERMISSION_LOCATION = 1;
    private EditText et_name;
    private Button btn_continue;
    private TextView tv_tracking_status;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_name = (EditText) findViewById(R.id.et_name);

        tv_tracking_status = (TextView) findViewById(R.id.tv_tracking_status);
        tv_tracking_status.setOnClickListener(this);

        btn_continue = (Button) findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        name = sharedPreferences.getString(Constant.NAME, "");

        if (name.length() == 0) {
            btn_continue.setVisibility(View.VISIBLE);
            et_name.setVisibility(View.VISIBLE);
            tv_tracking_status.setVisibility(View.GONE);
        } else {
            btn_continue.setVisibility(View.GONE);
            et_name.setVisibility(View.GONE);
            tv_tracking_status.setVisibility(View.VISIBLE);

            startTakingService();

            MapActivity.start(this);

            finish();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_continue:

                if (et_name.getText().toString().length() == 0) {
                    et_name.setError("Should not be empty!");
                    return;
                }

                if (null != sharedPreferences)
                    sharedPreferencesEditor = sharedPreferences.edit();

                sharedPreferencesEditor.putString(Constant.NAME, et_name.getText().toString());
                sharedPreferencesEditor.apply();

                startTakingService();

                btn_continue.setVisibility(View.GONE);
                et_name.setVisibility(View.GONE);
                tv_tracking_status.setVisibility(View.VISIBLE);

                break;
        }
    }

    private void startTakingService() {
        if (PermissionUtil.checkLocationPermission(this)) {
            LocationService.start(this);
        } else {
            ActivityCompat.requestPermissions(this, PermissionUtil.PERMISSIONS_LOCATION, REQUEST_PERMISSION_LOCATION);
        }
    }
}
