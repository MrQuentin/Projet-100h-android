package com.example.mrquentin.drawertest.BluetoothActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mrquentin.drawertest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class BluetoothFragment extends Fragment implements AdapterView.OnItemClickListener {

    private String TAG = BluetoothFragment.class.getSimpleName();
    private static BluetoothAdapter mBluetoothAdapter;

    private Map<String, BluetoothDevice> bluetoothDeviceList = new HashMap<>();
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private ListView devicesList;

    private static final long SCAN_PERIOD = 5000;

    DeviceListAdapter bluetoothDevicesAdapter;
    ListView listView;

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!bluetoothDeviceList.containsKey(device.getAddress())){
                                Log.d(TAG, "New Device Found : "+device.getName()+" :: "+device.getAddress());
                                bluetoothDeviceList.put(device.getAddress(), device);
                                //devices.add(device);
                                bluetoothDevicesAdapter.add(device);
                            }
                        }
                    });
                }
            };

    public BluetoothFragment() {
        // Required empty public constructor
    }

    public static BluetoothFragment newIntance(BluetoothAdapter bluetoothAdapter){
        BluetoothFragment bluetoothFragment = new BluetoothFragment();
        mBluetoothAdapter = bluetoothAdapter;
        return bluetoothFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bluetoothDevicesAdapter = new DeviceListAdapter(getContext() , R.layout.device_adapter_view, devices);;
        listView = (ListView) getActivity().findViewById(R.id.list);
        listView.setAdapter(bluetoothDevicesAdapter);
        listView.setOnItemClickListener(BluetoothFragment.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    ArrayList<BluetoothDevice> getArrayListFromMap(Map<String, BluetoothDevice> map){
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        Set<String> keys = map.keySet();
        for(String key : keys){
            devices.add(map.get(key));
        }
        return devices;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = devices.get(position).getName();
        String deviceAddress = devices.get(position).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            devices.get(position).createBond();
        }

    }
}