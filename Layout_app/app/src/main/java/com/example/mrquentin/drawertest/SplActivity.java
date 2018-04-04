package com.example.mrquentin.drawertest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.mrquentin.drawertest.Navigation.BluetoothFragment;

public class SplActivity extends AppCompatActivity {

    private String TAG = SplActivity.class.getSimpleName();
    private static int SPLASH_TIME_OUT = 1000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_LOC = 2;
    private static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;
    private static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;



    BluetoothManager bluetoothManager ;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_spl);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Wait ... !");
            }
        }, 500);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        BleLocationStart(this, this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothAdapter.isEnabled()){
                    if (((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        startMainActivity();
                    } else {
                        enableLocationWithIntent();
                    }
                }
            }
        }, 300);



        //start intent to enable bluetooth if not already on
        enableBluetoothWithIntent(mBluetoothAdapter);
    }

    void enableBluetoothWithIntent(BluetoothAdapter mBluetoothAdapter){
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    void enableLocationWithIntent(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Intent enableLocIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(enableLocIntent, REQUEST_ENABLE_LOC);
        }
    }

    void startMainActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplActivity.this, MainActivity.class);
                startActivityForResult(i,0);
                finish();
            }
        }, 300);
    }

    void BleLocationStart(Context context, Activity activity){

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions( activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                Log.d(TAG, "resultCode : "+resultCode);
                Toast.makeText(this, "resultCode :" + resultCode, Toast.LENGTH_LONG);
                if (resultCode == -1) {
                    if(((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        startMainActivity();
                    } else {
                        //lance la demande d'activation de la localisation apres 200 millis
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                enableLocationWithIntent();
                            }
                        }, 200);
                    }
                } else {
                    //relance la demande d'activation deu bluetooth apres 200 millis
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enableBluetoothWithIntent(mBluetoothAdapter);
                        }
                    }, 200);
                }
                break;
            case REQUEST_ENABLE_LOC:
                startMainActivity();
                break;
        }
    }



}
