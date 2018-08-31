package com.example.pnp.holoredearapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class ListDeviceAdapter extends RecyclerView.Adapter<ListDeviceAdapter.MyViewHolder>{
    private List<BluetoothDevice> listDevices;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener{
        void onItemClick(BluetoothDevice item);
    }

    public ListDeviceAdapter(List<BluetoothDevice> listDevices, OnItemClickListener listener){
        this.listDevices = listDevices;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_rows,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(listDevices.get(position),listener);
        BluetoothDevice device = listDevices.get(position);
    }

    @Override
    public int getItemCount() {
        return listDevices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView deviceName;

        public MyViewHolder(View view){
            super(view);
            deviceName = (TextView) view.findViewById(R.id.deviceField);

        }
        public void bind(final BluetoothDevice device, final OnItemClickListener listener){
            deviceName.setText(device.getName());
            Random rnd = new Random();
            float[] hsv = new float[3];
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f; // value component
            color = Color.HSVToColor(hsv);
            itemView.setBackgroundColor(color);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(device);
                }
            });
        }
    }
}
