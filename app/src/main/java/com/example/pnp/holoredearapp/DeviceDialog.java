package com.example.pnp.holoredearapp;

import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceDialog extends DialogFragment {

    private List<BluetoothDevice> listDevices;
    private MainActivity main;
    RecyclerView rv;
    ListDeviceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.dialog_layout,container);

        //RECYCER
        rv= (RecyclerView) rootView.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        listDevices = new ArrayList<BluetoothDevice>(bluetoothAdapter.getBondedDevices());
        adapter = new ListDeviceAdapter(listDevices, new ListDeviceAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BluetoothDevice item) {
                final BluetoothDevice device = (BluetoothDevice) item;
                main.setDevice(device);
                getDialog().dismiss();
            }
        });
        rv.setAdapter(adapter);// set adapter on recyclerview
        this.getDialog().setTitle("List Paired Device");

        return rootView;
    }
    public void setMain(MainActivity main){
        this.main = main;
    }
}
