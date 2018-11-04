package com.example.user.diapper;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {
    // Debugging
    private static final String TAG = "BluetoothService";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // RFCOMM Protocol
    private static final UUID MY_UUID = UUID
            .fromString("00000003-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter btAdapter;

    private Activity mActivity;
    private Handler mHandler;

    private ConnectThread mConnectThread; // ������ �ٽ�
    private ConnectedThread mConnectedThread; // ������ �ٽ�

    private int mState;

    // ���¸� ��Ÿ���� ���� ����
    private static final int STATE_NONE = 0; // we're doing nothing
    private static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    private static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    private static final int STATE_CONNECTED = 3; // now connected to a remote
    // device

    // Constructors
    public BluetoothService(Activity ac, Handler h) {
        mActivity = ac;
        mHandler = h;

        // BluetoothAdapter ���
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Check the Bluetooth support
     *
     * @return boolean
     */
    public boolean getDeviceState() {
        //Log.i(TAG, "Check the Bluetooth support");

        if (btAdapter == null) {
           // Log.d(TAG, "Bluetooth is not available");

            return false;

        } else {
          //  Log.d(TAG, "Bluetooth is available");

            return true;
        }
    }

    /**
     * Check the enabled Bluetooth
     */
    public int checkBluetooth(){
        int b;
        if(btAdapter.isEnabled())
        {
            b=0;
        }
        else{
            b=1;
        }
        return b;
    }

    public void enableBluetooth() {


        Log.i(TAG, "Check the enabled Bluetooth");
        //Toast.makeText(mActivity.getApplicationContext(), "WHy?", Toast.LENGTH_SHORT).show();
        if (btAdapter.isEnabled()) {
            // ����� ������� ���°� On�� ���
           Log.d(TAG, "Bluetooth Enable Now");
           // Toast.makeText(mActivity.getApplicationContext(), "Bluetooth Enable Now", Toast.LENGTH_SHORT).show();

            // Next Step
           // scanDevice();
            btAdapter.disable();


        } else {
            // ����� ������� ���°� Off�� ���
            Log.d(TAG, "Bluetooth Enable Request");
           // Toast.makeText(mActivity.getApplicationContext(), "Bluetooth Enable Request", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);



        }
    }

    /**
     * Available device search
     */
    public void scanDevice() {
        Log.d(TAG, "Scan Device");

        Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
        mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    /**
     * after scanning and get device info
     *
     * @param data
     */
    public void getDeviceInfo(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(
                DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        // BluetoothDevice device = btAdapter.getRemoteDevice(address);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        Log.d(TAG, "Get Device Info \n" + "address : " + address);

        connect(device);
    }

    // Bluetooth ���� set
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    // Bluetooth ���� get
    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread == null) {

        } else {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {

        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    // ConnectThread �ʱ�ȭ device�� ��� ���� ����
    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread == null) {

            } else {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {

        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);

        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    // ConnectedThread �ʱ�ȭ
    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread == null) {

        } else {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {

        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    // ��� thread stop
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    // ���� ���� �κ�(������ �κ�)
    public void write(byte[] out) { // Create temporary object
        ConnectedThread r; // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        } // Perform the write unsynchronized r.write(out); }
    }

    // ���� ����������
    private void connectionFailed() {
        setState(STATE_LISTEN);
    }

    // ������ �Ҿ��� ��
    private void connectionLost() {
        setState(STATE_LISTEN);

    }



    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

			/*
			 * / // Get a BluetoothSocket to connect with the given
			 * BluetoothDevice try { // MY_UUID is the app's UUID string, also
			 * used by the server // code tmp =
			 * device.createRfcommSocketToServiceRecord(MY_UUID);
			 *
			 * try { Method m = device.getClass().getMethod(
			 * "createInsecureRfcommSocket", new Class[] { int.class }); try {
			 * tmp = (BluetoothSocket) m.invoke(device, 15); } catch
			 * (IllegalArgumentException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } catch (IllegalAccessException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } catch
			 * (InvocationTargetException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }
			 *
			 * } catch (NoSuchMethodException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); } } catch (IOException e) { } /
			 */

            // ����̽� ������ �� BluetoothSocket ����
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // ������ �õ��ϱ� ������ �׻� ��� �˻��� �����Ѵ�.
            // ��� �˻��� ��ӵǸ� ����ӵ��� �������� �����̴�.
            btAdapter.cancelDiscovery();

            // BluetoothSocket ���� �õ�
            try {
                // BluetoothSocket ���� �õ��� ���� return ���� succes �Ǵ� exception�̴�.
                mmSocket.connect();
                Log.d(TAG, "Connect Success");

            } catch (IOException e) {
                connectionFailed(); // ���� ���н� �ҷ����� �޼ҵ�
                Log.d(TAG, "Connect Fail");

                // socket�� �ݴ´�.
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG,
                            "unable to close() socket during connection failure",
                            e2);
                }
                // ������? Ȥ�� ���� �������� �޼ҵ带 ȣ���Ѵ�.
                BluetoothService.this.start();
                return;
            }

            // ConnectThread Ŭ������ reset�Ѵ�.
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // ConnectThread�� �����Ѵ�.
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // BluetoothSocket�� inputstream �� outputstream�� ��´�.
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // InputStream���κ��� ���� �޴� �д� �κ�(���� �޴´�)
                    bytes = mmInStream.read(buffer);

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer
         *            The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                // ���� ���� �κ�(���� ������)
                mmOutStream.write(buffer);

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

}
/*
import android.bluetooth.BluetoothAdapter;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {
    // Debugging
    private static final String TAG = "BluetoothService";


    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;
    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // RFCOMM Protocol
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter btAdapter;
    private ConnectThread mConnectThread; // 변수명 다시
    private ConnectedThread mConnectedThread; // 변수명 다시


    public boolean getDeviceState() {

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG, "Check the Bluetooth support");
        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth is not available");
            return false;
        } else {
            Log.d(TAG, "Bluetooth is available");
            return true;
        }
    }

    public void enableBluetooth() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.i(TAG, "Check the enabled Bluetooth");
        if (btAdapter != null) {
            // 기기의 블루투스 상태가 On인 경우
            Log.d(TAG, "Bluetooth Enable Now");
            // Next Step
            scanDevice();
        } else {
            // 기기의 블루투스 상태가 Off인 경우
            Log.d(TAG, "Bluetooth Enable Request");
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetoot returns
                if (resultCode == Activity.RESULT_OK) {
                    // / 확인 눌렀을 때
                    // Next Step
                    btService.scanDevice();
                } else {
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

    public void scanDevice() {
        Log.d(TAG, "Scan Device");
        Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
        mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
    ///검색된 기기에 접속하기

    public void getDeviceInfo(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        // BluetoothDevice device = btAdapter.getRemoteDevice(address);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        Log.d(TAG, "Get Device Info \n" + "address : " + address);
        connect(device);
    }///address라는 String에는 선택한 기기의 주소가 담겨져있따

    ////////////////////연결
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // BluetoothSocket의 inputstream 과 outputstream을 얻는다.
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // InputStream으로부터 값을 받는 읽는 부분(값을 받는다)
                    bytes = mmInStream.read(buffer);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }


        public void write(byte[] buffer) {
            try {
                // 값을 쓰는 부분(값을 보낸다)
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}

*/