package com.example.firstapp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BluetoothBroadcastReceiver extends BroadcastReceiver {

	public BluetoothBroadcastReceiver() {
		
		
	}
	
	public void discoveryCompletedCallback() {
		
	}

	public void deviceDiscoveredCallback(BluetoothDevice device) {
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		String action = intent.getAction();
		// When discovery finds a device
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		    // Get the BluetoothDevice object from the Intent
		    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    // Callback
		    deviceDiscoveredCallback(device);
		    
		} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			
			discoveryCompletedCallback();
		}
	}
}
