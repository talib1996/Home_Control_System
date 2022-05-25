package com.example.Home_Control_System;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //private boolean alreadyConnected;
    //private String MAC_ADDRESS ;
    ClientClass clientClass = null;
    SendReceive sendReceive;
    //PairedDevices pairedDevices;
    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> arrayAdapterNewDevices;
    ListView listView2;
    ArrayList newDeviceList;
    TextView scanning_status, connectionStatus, label_for_toggleButton1, label_for_toggleButton2;
    Button reScanButton, scanButton, disConnectButton, logout_button;
    ToggleButton toggleButton1, toggleButton2;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //pairedDevices = new PairedDevices();
        //pairedDevices.setMAC_ADDRESS(MAC_ADDRESS);
        scanning_status = findViewById(R.id.scanning_status);
        listView2 = findViewById(R.id.listview2);
        progressBar = findViewById(R.id.progressBar);
        reScanButton = findViewById(R.id.reScanButton);
        toggleButton1 = findViewById(R.id.toggleButton1);
        toggleButton2 = findViewById(R.id.toggleButton2);
        progressBar.setVisibility(View.GONE);
        scanButton = findViewById(R.id.scanDevices);
        connectionStatus = findViewById(R.id.connectionStatus);
        label_for_toggleButton1 = findViewById(R.id.textview_switch_1);
        label_for_toggleButton2 = findViewById(R.id.textview_switch_2);
        disConnectButton = findViewById(R.id.disconnectButton);
        logout_button = findViewById(R.id.log_out_button);
        getBluetoothAdapter();
        //bluetoothAdapter.startDiscovery();
        showUnPairedDevices();
        implementedListeners();


    }

    private final BroadcastReceiver deviceFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

                if (bluetoothAdapter.isDiscovering()){

                    scanning_status.setText(R.string.label_scanning);
                    //scanButton.setText("Scanning");
                    reScanButton.setEnabled(false);
                    if (scanButton.getVisibility() == View.GONE){
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

             if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //PairedDevices pairedDevices = new PairedDevices();
                //pairedDevices.getPairedDevices();
                String deviceName = device.getName();
                String devicehardwareaddress = device.getAddress();
                 String connected_or_notConnected, paired_or_notPaired;
                 if (device.getBondState()==BluetoothDevice.BOND_BONDED){
                    paired_or_notPaired = "paired";
                    connected_or_notConnected = "not connected";
                    if (!newDeviceList.contains(deviceName + "\n" + devicehardwareaddress+"\n"+paired_or_notPaired+", "+ connected_or_notConnected)) {
                        newDeviceList.add(deviceName + "\n" + devicehardwareaddress+"\n"+paired_or_notPaired+", "+ connected_or_notConnected);
                        arrayAdapterNewDevices.notifyDataSetChanged();
                    }
                }else if (device.getBondState()==BluetoothDevice.BOND_NONE){
                    paired_or_notPaired = "not paired";
                    if (!newDeviceList.contains(deviceName + "\n" + devicehardwareaddress+"\n"+ paired_or_notPaired)) {
                        newDeviceList.add(deviceName + "\n" + devicehardwareaddress+"\n"+ paired_or_notPaired);
                        arrayAdapterNewDevices.notifyDataSetChanged();
                    }
                }


            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                 scanning_status.setText(R.string.label_scanned);
                 reScanButton.setText(R.string.label_scan_devices_again);
                 reScanButton.setEnabled(true);
                 progressBar.setVisibility(View.GONE);
             }
             else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                 final int currentBondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                 final int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
                 switch(currentBondState){
                     case 10:
                         if (previousBondState == 11){
                             Toast.makeText(context, "Could not Paired", Toast.LENGTH_SHORT).show();
                         }else if (previousBondState ==12){
                             Toast.makeText(context, "Un paired", Toast.LENGTH_SHORT).show();
                             connectionStatus.setTextColor(Color.RED);
                             connectionStatus.setText("Disconnected");
                             scanButton.setEnabled(true);
                             arrayAdapterNewDevices.clear();
                             arrayAdapterNewDevices.notifyDataSetChanged();
                             //reScanButton.callOnClick();
                         }
                         break;
                     case 11:
                         if (previousBondState == 10){
                             Toast.makeText(context, "Pairing ...", Toast.LENGTH_SHORT).show();
                         }
                         break;
                     case 12:
                         if (previousBondState == 11){
                             Toast.makeText(context, "Paired", Toast.LENGTH_SHORT).show();
                             arrayAdapterNewDevices.clear();
                             arrayAdapterNewDevices.notifyDataSetChanged();
                             bluetoothAdapter.startDiscovery();
                         }
                         break;
                 }
             }
        }

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(deviceFoundReceiver);
        bluetoothAdapter.disable();
    }

    @Override
    protected void onResume() {
        //refreshDevices();
        super.onResume();
        bluetoothAdapter.enable();
        if (clientClass != null)
        {

            Toast.makeText(MainActivity.this, "connected already", Toast.LENGTH_SHORT).show();
        }
        //BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
        //ClientClass clientClass1 = new ClientClass(bluetoothDevice);
        //clientClass1.start();

        registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));

    }
    public void getBluetoothAdapter(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void showUnPairedDevices(){
        newDeviceList = new ArrayList<>();
        arrayAdapterNewDevices = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,newDeviceList);
        listView2.setAdapter(arrayAdapterNewDevices);

    }
    public void implementedListeners(){
        listView2.setOnItemClickListener((adapterView, view, i, l) -> {
            String string = listView2.getAdapter().getItem(i).toString();
            String []tokens = string.split("\n");
            if (!bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            }
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(tokens[1]);

            if(bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                boolean bond = bluetoothDevice.createBond();
                if(bond){
                    Toast.makeText(this, "Starting to pair", Toast.LENGTH_SHORT).show();
                }
            }
            else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                Toast.makeText(MainActivity.this, "Already Paired ...\nNow trying to connect ...", Toast.LENGTH_SHORT).show();
                clientClass = new ClientClass(bluetoothDevice);
                clientClass.start();
            }
        });
        reScanButton.setOnClickListener(view -> {
            newDeviceList = new ArrayList<>();
            arrayAdapterNewDevices = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, newDeviceList);
            listView2.setAdapter(arrayAdapterNewDevices);
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 0);
            }

            bluetoothAdapter.startDiscovery();

        });
        toggleButton1.setOnClickListener(view -> {
            String string ;
            if(toggleButton1.getText().equals("ON")){
                string = "A";

            }else{
                string = "a";
            }
            if(clientClass != null && bluetoothAdapter.isEnabled()){
                sendReceive = new SendReceive(clientClass.socket);
                sendReceive.write(string.getBytes());
                
            }
            else{
                Toast.makeText(MainActivity.this, "You are not connected with a bluetooth device or bluetooth is turned off", Toast.LENGTH_SHORT).show();
            }


        });
        toggleButton2.setOnClickListener(view -> {
            String string ;
            if(toggleButton2.getText().equals("ON")){
                string = "B";

            }else{
                string = "b";
            }
            if(clientClass != null && bluetoothAdapter.isEnabled()){
                sendReceive = new SendReceive(clientClass.socket);
                sendReceive.write(string.getBytes());
            }
            else{
                Toast.makeText(MainActivity.this, "You are not connected with a bluetooth device or bluetooth is turned off", Toast.LENGTH_SHORT).show();
            }

        });
        disConnectButton.setOnClickListener(view -> {
            try {
                if(clientClass != null){
                    clientClass.socket.close();
                    clientClass = null;
                    connectionStatus.setTextColor(Color.RED);
                    connectionStatus.setText(R.string.label_disconnected);
                    scanButton.setEnabled(true);
                }else{
                    Toast.makeText(MainActivity.this, "Already Disconnected", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        scanButton.setOnClickListener(view -> {
            if (!bluetoothAdapter.isEnabled()) {
                getBluetoothAdapter();
            }

            scanButton.setVisibility(View.GONE);
            connectionStatus.setVisibility(View.GONE);
            toggleButton1.setVisibility(View.GONE);
            toggleButton2.setVisibility(View.GONE);
            disConnectButton.setVisibility(View.GONE);
            label_for_toggleButton1.setVisibility(View.GONE);
            label_for_toggleButton2.setVisibility(View.GONE);
            reScanButton.setVisibility(View.VISIBLE);
            scanning_status.setVisibility(View.VISIBLE);
            listView2.setVisibility(View.VISIBLE);


            bluetoothAdapter.startDiscovery();


        });
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {

                case State.STATE_CONNECTED:
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    reScanButton.setVisibility(View.GONE);
                    scanning_status.setVisibility(View.GONE);
                    listView2.setVisibility(View.GONE);
                    listView2.setVisibility(View.GONE);
                    scanButton.setVisibility(View.VISIBLE);
                    connectionStatus.setTextColor(getResources().getColor(R.color.green));
                    connectionStatus.setText(R.string.label_connected);
                    scanButton.setEnabled(false);
                    connectionStatus.setVisibility(View.VISIBLE);
                    toggleButton1.setVisibility(View.VISIBLE);
                    toggleButton2.setVisibility(View.VISIBLE);
                    label_for_toggleButton1.setVisibility(View.VISIBLE);
                    label_for_toggleButton2.setVisibility(View.VISIBLE);
                    disConnectButton.setVisibility(View.VISIBLE);


                    break;
                case State.STATE_CONNECTION_FAILED:
                    Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    connectionStatus.setTextColor(Color.RED);
                    connectionStatus.setText("Disconnected");
                    break;
                    /*
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    msg_box.setText(tempMsg);
                    break;
                    */

            }
            return true;
        }

    });

    private class ClientClass extends Thread {
        private final BluetoothDevice device;
        private BluetoothSocket socket;
        public ClientClass(BluetoothDevice device1) {
            device = device1;

            try {
                BluetoothDevice HC05 = bluetoothAdapter.getRemoteDevice(device.getAddress());
                socket = HC05.createRfcommSocketToServiceRecord(MY_UUID);
                //socket = device.createRfcommSocketToServiceRecord(UUID.fromString(device.getAddress()));
                Toast.makeText(MainActivity.this, socket.toString(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void run() {

            try {
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                Message message = Message.obtain();
                message.what = com.example.Home_Control_System.State.STATE_CONNECTED;
                handler.sendMessage(message);
                //MAC_ADDRESS = device.getAddress();
                //sendReceive = new SendReceive(socket);
                //sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = com.example.Home_Control_System.State.STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }

        }



    }
    private static class SendReceive extends Thread {
        //private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            //InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                //  tempIn = bluetoothSocket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //inputStream = tempIn;
            outputStream = tempOut;
        }

/*
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        */


        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bluetoothAdapter.disable();
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);

                    }
                })
                .setNegativeButton("No", null)
                .show();
        //super.onBackPressed();
        //Toast.makeText(this, "you pressed back", Toast.LENGTH_SHORT).show();


    }
}

