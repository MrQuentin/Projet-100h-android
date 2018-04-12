package com.example.mrquentin.drawertest.Helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MrQuentin on 09/04/2018.
 */

public class ActivityDataHolder {

    //Activity View
    private View view;

    //Gestion Bluetooth
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private Map<String, BluetoothDevice> bluetoothDeviceList = new HashMap<>();
    private static final long SCAN_PERIOD = 5000;

    //widgets
    private Toolbar toolbar;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public ActivityDataHolder(View view) {
        this.view = view;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public boolean ismScanning() {
        return mScanning;
    }

    public void setmScanning(boolean mScanning) {
        this.mScanning = mScanning;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public Map<String, BluetoothDevice> getBluetoothDeviceList() {
        return bluetoothDeviceList;
    }

    public void setBluetoothDeviceList(Map<String, BluetoothDevice> bluetoothDeviceList) {
        this.bluetoothDeviceList = bluetoothDeviceList;
    }

    public static long getScanPeriod() {
        return SCAN_PERIOD;
    }
}
