package com.example.user.diapper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

//import android.bluetooth.BluetoothAdapter;

//import android.bluetooth.BluetoothAdapter;

/**
 * Created by user on 2018-10-12.
 */

public class BluetoothService {
    // Debugging
    private static final String TAG = "BluetoothService";


    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;
// Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;



    public boolean getDeviceState()
    {

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG, "Check the Bluetooth support");
        if(btAdapter == null)
        {
            Log.d(TAG, "Bluetooth is not available");
            return false;
        } else
        {
            Log.d(TAG, "Bluetooth is available");
            return true;
        }
    }
    public void enableBluetooth()
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.i(TAG, "Check the enabled Bluetooth");
        if(btAdapter!=null)
        {
            // 기기의 블루투스 상태가 On인 경우
            Log.d(TAG, "Bluetooth Enable Now");
            // Next Step
            } else
            {
                // 기기의 블루투스 상태가 Off인 경우
                Log.d(TAG, "Bluetooth Enable Request");
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
            }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetoot returns
                if (resultCode == Activity.RESULT_OK) {
                    // / 확인 눌렀을 때
                    // Next Step
                }
                else {
                    // 취소 눌렀을 때
                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }


    // Constructors
    public BluetoothService(MainActivity ac, Handler h) {
        mActivity = ac;
        mHandler = h;
        // BluetoothAdapter 얻기
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }
}