package com.example.mrquentin.drawertest.Helper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mrquentin.drawertest.BluetoothActivity.BluetoothFragment;
import com.example.mrquentin.drawertest.BluetoothTabActivity;
import com.example.mrquentin.drawertest.Dialogs.DialogHandler;
import com.example.mrquentin.drawertest.GoogleMapsGuideActivity.MapsActivity;
import com.example.mrquentin.drawertest.MainActivity;
import com.example.mrquentin.drawertest.Navigation.DashboardFragment;
import com.example.mrquentin.drawertest.R;

/**
 * Created by MrQuentin on 09/04/2018.
 */

public class InitHelper {

    private static final String TAG = "InitHelper";

    public void initDrawer(Toolbar toolbar, Activity activity,FragmentManager fragmentManager){

        Log.d(TAG, "initDrawer: init drawer start");
        
        final FragmentManager mFragmentManager = fragmentManager;

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_recent) {
                    new DialogHandler().showBasicTextDialog("recent", "recent content", mFragmentManager);
                } else if (id == R.id.nav_settings) {
                    new DialogHandler().showBasicTextDialog("Settings", "settings content", mFragmentManager);
                } else if (id == R.id.nav_help) {
                    new DialogHandler().showBasicTextDialog("Help", "Help content here", mFragmentManager);
                } else if (id == R.id.nav_about) {
                    new DialogHandler().showBasicTextDialog("About", "About content here", mFragmentManager);
                } else if (id == R.id.nav_house) {
                    new DialogHandler().showBasicTextDialog("My home", "My home Content here", mFragmentManager);
                } else if (id == R.id.nav_work) {
                    new DialogHandler().showBasicTextDialog("My work", "My work content here", mFragmentManager);
                }
                //close drawer onclick
                //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                //drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    public Toolbar initToolBar(Activity activity){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setTitle("");//remove app title
        return toolbar;
    }

    public BluetoothAdapter initBLE(Activity activity){
        final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getAdapter();
    }

    public void initNavigation(ActivityDataHolder dataHolder, Activity activity, FragmentManager fragmentManager){
        final Activity act = activity;
        final ActivityDataHolder activityDataHolder = dataHolder;
        final FragmentManager manager = fragmentManager;
        TextView mTextMessage = (TextView) activity.findViewById(R.id.messages);
        BottomNavigationView navigation = (BottomNavigationView) activity.findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent intent;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Class fragmentClass;

                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        Log.d(TAG, "onNavigationItemSelected: Going to Home Tab");
                        intent = new Intent(act, MapsActivity.class);
                        break;
                    case R.id.navigation_dashboard:
                        Log.d(TAG, "onNavigationItemSelected: Going to Dash Tab");
                        intent = new Intent(act, BluetoothTabActivity.class);
                        break;
                    case R.id.navigation_bluetooth:
                        Log.d(TAG, "onNavigationItemSelected: Going to Home Tab");
                        intent = new Intent(act, BluetoothTabActivity.class);
                        break;
                    default:
                        Log.d(TAG, "onNavigationItemSelected: Default Tab");
                        intent = new Intent(act, BluetoothTabActivity.class);
                        return false;
                }
                act.startActivity(intent);
                act.finish();
                return true;
            }
        });

        manager.beginTransaction().replace(R.id.flContent, new DashboardFragment()).commit();
    }

}
