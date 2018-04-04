package com.example.mrquentin.drawertest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrquentin.drawertest.Dialogs.DialogHandler;
import com.example.mrquentin.drawertest.Navigation.BluetoothFragment;
import com.example.mrquentin.drawertest.Navigation.DashboardFragment;
import com.example.mrquentin.drawertest.Navigation.HomeFragment;
import com.example.mrquentin.drawertest.Dialogs.Fragments.BasicTextDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = MainActivity.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private Map<String, BluetoothDevice> bluetoothDeviceList = new HashMap<>();

    private static final long SCAN_PERIOD = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");//remove app title
        setSupportActionBar(toolbar);

     /*==========================================================================================
                                          Floating action button
      ==========================================================================================*/


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

     /*==========================================================================================
                                                 Drawer
      =========================================================================================*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        /*==========================================================================================
                                                Navigation
         =========================================================================================*/
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new HomeFragment()).commit();

     /*==============================================================================================
                                                    BLE
     =============================================================================================*/

     final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
     mBluetoothAdapter = bluetoothManager.getAdapter();

    }




    /*==============================================================================================
                                                Drawer Button
     =============================================================================================*/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*==============================================================================================
                                          Suppression menu 3 point
     =============================================================================================*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /*==============================================================================================
                                            ActionBar selector
     =============================================================================================*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /*==============================================================================================
                                         Drawer Selector
     =============================================================================================*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recent) {
            new DialogHandler().showBasicTextDialog("recent", "recent content", getSupportFragmentManager());
        } else if (id == R.id.nav_settings) {
            new DialogHandler().showBasicTextDialog("Settings", "settings content", getSupportFragmentManager());
        } else if (id == R.id.nav_help) {
            new DialogHandler().showBasicTextDialog("Help", "Help content here", getSupportFragmentManager());
        } else if (id == R.id.nav_about) {
            new DialogHandler().showBasicTextDialog("About", "About content here", getSupportFragmentManager());
        } else if (id == R.id.nav_house) {
            new DialogHandler().showBasicTextDialog("My home", "My home Content here", getSupportFragmentManager());
        } else if (id == R.id.nav_work) {
            new DialogHandler().showBasicTextDialog("My work", "My work content here", getSupportFragmentManager());
        }

        //close drawer onclick
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*==============================================================================================
                                             Navigation
        ==============================================================================================*/
    private TextView mTextMessage;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;
            Class fragmentClass;

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    System.out.println("NavBar home");
                    try {
                        fragment = (Fragment) HomeFragment.class.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.navigation_dashboard:
                    System.out.println("Nav DashBoard");
                    try {
                        fragment = (Fragment) DashboardFragment.class.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.navigation_bluetooth:
                    System.out.println("Nav Bluetooth");
                    try {
                        fragment = (Fragment) BluetoothFragment.newIntance(mBluetoothAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Default");
                    try {
                        fragment = (Fragment) HomeFragment.class.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
            }


            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            return true;
        }
    };

}
