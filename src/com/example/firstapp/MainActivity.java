package com.example.firstapp;

import java.io.IOException;
import java.util.UUID;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	// TODO these should probably not be hardcoded here
	private final static String serverBTName = "DisasterRadio";
	private final static UUID serverUUID = UUID.fromString("2d9883d8-7d88-4ca0-a444-6a8cfe2087d7");
	
	private final static int REQUEST_ENABLE_BT = 100;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothBroadcastReceiver mReceiver;
	private BluetoothSocket sock;

	private TextView statusText; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        statusText = (TextView) findViewById(R.id.StatusText);
        statusText.setText("Initializing.\n");
        
        
        statusText.append("Checking for bluetooth support...\n");
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        	statusText.append("Device doest not appear to support bluetooth :(\n");
        } else {
        	statusText.append("Bluetooth support detected!\n");
        }
        
        if (!mBluetoothAdapter.isEnabled()) {
        	statusText.append("Enabling bluetooth...\n");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
        	statusText.append("Bluetooth was already enabled.\n");
        	prepareDiscovery();
        }
    }
    
    private void prepareDiscovery() {
    	// Register the BroadcastReceiver
    	
    	mReceiver = new BluetoothBroadcastReceiver() {
        	public void discoveryCompletedCallback() {
        		statusText.append("Existing discovery cancelled\n");
        		beginDiscovery();
        		unregisterReceiver(mReceiver);
        	}
        };
    	
        if (mBluetoothAdapter.isDiscovering()){
        	statusText.append("Cancelling existing discovery\n");
        	IntentFilter filter = new IntentFilter();
        	filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        	registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        	mBluetoothAdapter.cancelDiscovery();
        } else {
        	beginDiscovery();
        }
    }
    
    private void beginDiscovery() {
    	// Register the BroadcastReceiver
    	
    	mReceiver = new BluetoothBroadcastReceiver() {
        	public void deviceDiscoveredCallback(BluetoothDevice device) {
        		statusText.append("Found device: " + device.getName() + " - " + device.getAddress() + "\n");
        		checkDevice(device);
        	}
        	public void discoveryCompletedCallback() {
        		statusText.append("Discovery ended\n");
        		unregisterReceiver(mReceiver);
        	}
        };
    	
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(BluetoothDevice.ACTION_FOUND);
    	filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    	
    	registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    	
    	statusText.append("Starting discovery\n");
    	mBluetoothAdapter.startDiscovery();
    }

    private void checkDevice(BluetoothDevice device) {
    	
    	if(device.getName().equals(serverBTName)) {
    		unregisterReceiver(mReceiver);
    		mBluetoothAdapter.cancelDiscovery();
    		statusText.append("Found Disaster Radio server!\n");
    		initComms(device);
    	}
    }
    
    private void initComms(BluetoothDevice device) {
    	try {
    		statusText.append("Opening socket...\n");
//			sock = device.createInsecureRfcommSocketToServiceRecord(serverUUID);
    		sock = device.createRfcommSocketToServiceRecord(serverUUID);
			sock.connect();
			statusText.append("Socket open!\n");
			//sock.getOutputStream().write(100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			statusText.append("Socket creation failed.\n");
			e.printStackTrace();
		}
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	statusText = (TextView) findViewById(R.id.StatusText);
    	
    	if(requestCode == REQUEST_ENABLE_BT) {
    		
    		if(resultCode == RESULT_OK) {
    			statusText.append("Bluetooth enabled!\n");
    			prepareDiscovery();
    		} else {
    			statusText.append("Enabling bluetooth failed.\n");
    		}
    	
    	}
    	
    }
    
    public void onDestroy() {
    	unregisterReceiver(mReceiver);
    	super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
