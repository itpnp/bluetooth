package com.example.pnp.holoredearapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends Activity {
    //    private final String DEVICE_NAME="H-C-2010-06-01(3366)";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    Button startButton, deviceButton,clearButton,stopButton;
    TextView textView, labelDescription, deviceName;
    EditText editText;
    ImageView imageView;
    private int readIndex;
    private boolean getReady = false;
    private String data;
    boolean deviceConnected=false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.buttonStart);
//        sendButton = (Button) findViewById(R.id.buttonSend);
        clearButton = (Button) findViewById(R.id.buttonClear);
        stopButton = (Button) findViewById(R.id.buttonStop);
        deviceButton = (Button) findViewById(R.id.chooseDeviceButton);
        deviceName = (TextView)findViewById(R.id.deviceNames);
        final FragmentManager fm=getFragmentManager();
        final DeviceDialog tv=new DeviceDialog();
        tv.setMain(this);
        BTinit();
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.show(fm,"device_tag");
            }
        });
//        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        labelDescription = (TextView) findViewById(R.id.labelDescription);
        imageView = (ImageView) findViewById(R.id.borobudur);
        setUiEnabled(false);
    }

    public void setDevice(BluetoothDevice device){
        this.device = device;
        deviceName.setText(this.device.getName());
    }
    public void setUiEnabled(boolean bool)
    {
        startButton.setEnabled(!bool);
//        sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);

    }

    public boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            found = false;
            Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
        }else{
            if(!bluetoothAdapter.isEnabled())
            {
                Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableAdapter, 0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            found = true;
        }

//        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
//        if(bondedDevices.isEmpty())
//        {
//            Toast.makeText(getApplicationContext(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();
//        }else{
//            for (BluetoothDevice iterator : bondedDevices)
//            {
//                if(iterator.getName().equals("HOLOGRAM_READER"))
//                {
//                    device=iterator;
//                    found=true;
//                    break;
//                }
//            }
//        }
        return found;
    }

    public boolean BTconnect()
    {
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return connected;
    }

    public void onClickStart(View view) {
        if(BTinit())
        {
            if(BTconnect())
            {
                setUiEnabled(true);
                deviceConnected=true;
                beginListenForData();
                textView.append("\nConnection Opened!\n");
            }

        }
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string=new String(rawBytes,"US-ASCII");
                            data = data+""+string;
                            handler.post(new Runnable() {
                                public void run() {
                                    if (data.contains("TERBACA")) {
                                        textView.setText("");
                                        textView.append("Verified Hologram made by PURA GROUP, INDONESIA");
                                        imageView.setVisibility(View.VISIBLE);
                                        labelDescription.setVisibility(View.VISIBLE);
                                        data = "";
                                    } else if (data.contains("GAGAL")) {
                                        textView.setText("");
                                        textView.append("UNVERIFIED !!");
                                        imageView.setVisibility(View.INVISIBLE);
                                        labelDescription.setVisibility(View.INVISIBLE);
                                        data = "";
                                    } else if (data.contains("COBALAGI")) {
                                        textView.setText("");
                                        textView.append("Please Try Again !");
                                        imageView.setVisibility(View.INVISIBLE);
                                        labelDescription.setVisibility(View.INVISIBLE);
                                        data = "";
                                    } else if (data.contains("BorBud")) {
                                        textView.setText("");
                                        textView.append("Verified Hologram made by PURA GROUP, INDONESIA");
                                        imageView.setVisibility(View.VISIBLE);
                                        labelDescription.setVisibility(View.VISIBLE);
                                        data = "";
                                    } else if (data.contains("TA2016")) {
                                        textView.setText("");
                                        textView.append("PITA CUKAI TA 2016");
                                        imageView.setVisibility(View.INVISIBLE);
                                        labelDescription.setVisibility(View.INVISIBLE);
                                        data = "";
                                    }else if (data.contains("TA2017")) {
                                        textView.setText("");
                                        textView.append("PITA CUKAI TA 2017");
                                        imageView.setVisibility(View.INVISIBLE);
                                        labelDescription.setVisibility(View.INVISIBLE);
                                        data = "";
                                    }else if (data.contains("TA2018")) {
                                        textView.setText("");
                                        textView.append("PITA CUKAI TA 2018");
                                        imageView.setVisibility(View.INVISIBLE);
                                        labelDescription.setVisibility(View.INVISIBLE);
                                        data = "";
                                    } else if (data.contains("COBALG")) {
                                        textView.setText("");
                                        textView.append("Please Try Again !");
                                        imageView.setVisibility(View.INVISIBLE);
                                        labelDescription.setVisibility(View.INVISIBLE);
                                        data = "";
                                    }
                                }
                                });
                        }
                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });
        thread.start();
    }

    public void onClickSend(View view) {
        String string = editText.getText().toString();
        string.concat("\n");
        try {
            outputStream.write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.append("\nSent Data:"+string+"\n");
    }

    public void onClickStop(View view) throws IOException {
        stopThread = true;
        outputStream.close();
        inputStream.close();
        socket.close();
        setUiEnabled(false);
        deviceConnected=false;
        textView.append("\nConnection Closed!\n");
        imageView.setVisibility(View.INVISIBLE);
        labelDescription.setVisibility(View.INVISIBLE);
    }

    public void onClickClear(View view) {
        imageView.setVisibility(View.INVISIBLE);
        labelDescription.setVisibility(View.INVISIBLE);
        textView.setText("");
    }
}

