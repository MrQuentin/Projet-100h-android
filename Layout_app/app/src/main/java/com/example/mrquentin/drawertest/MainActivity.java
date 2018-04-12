package com.example.mrquentin.drawertest;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.mrquentin.drawertest.Helper.ActivityDataHolder;
import com.example.mrquentin.drawertest.Helper.InitHelper;

public class MainActivity extends AppCompatActivity{

    private String TAG = MainActivity.class.getSimpleName();

    private InitHelper mInitHelper = new InitHelper();
    private ActivityDataHolder mActivityDataHolder ; 


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*==============================================================================================
                                          Initialisation
        =============================================================================================*/
        
        //init DataHolder
        mActivityDataHolder = new ActivityDataHolder(mInitHelper.initToolBar(this));

        //initialise la Toolbar
        Log.d(TAG, "onCreate: setting up toolbar");
        mActivityDataHolder.setToolbar(mInitHelper.initToolBar(this));
        this.setSupportActionBar(mActivityDataHolder.getToolbar());

        //init drawer
        Log.d(TAG, "onCreate: starting drawer init");
        mInitHelper.initDrawer(mActivityDataHolder.getToolbar(), this, getSupportFragmentManager());

        //init BLE
        Log.d(TAG, "onCreate: start BLE init");
        mActivityDataHolder.setmBluetoothAdapter(mInitHelper.initBLE(this));

        //init Naviguation
        Log.d(TAG, "onCreate: starting naviguation init");
        mInitHelper.initNavigation(mActivityDataHolder, this, getSupportFragmentManager());
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

        return super.onOptionsItemSelected(item);
    }
}