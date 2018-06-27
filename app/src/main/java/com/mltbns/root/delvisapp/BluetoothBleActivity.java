package com.mltbns.root.delvisapp;

/*
* https://blog.csdn.net/zw1996/article/details/75168742
*https://www.2cto.com/kf/201707/659553.html
*https://blog.csdn.net/a1054751988/article/details/51054441
*https://blog.csdn.net/qq_35414804/article/details/53352205
* https://blog.csdn.net/Small_Lee/article/details/50899743
* */

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.icu.text.SymbolTable;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;




public class BluetoothBleActivity extends AppCompatActivity {

    private Toast   mToast;
    private Button mBtnSend;
    private Button mBtnDisconnect;
    private Button mBtnScan;
    private Button mBtnClear;
    private Button mBtnOpen;
    private Button mBtnClose;
    private ListView mLvDeviceList;
    private TextView mTextView;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private BluetoothDeviceAdapter mBluetoothDeviceAdapter;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothSocket socket;
    private List<BluetoothDevice> mBlueList = new ArrayList<>();
    private Context context;
    private Handler mUIHandler = new MyHandler();
    private EditText    mEditText;


    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE=1;
    private static  final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION  = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_ble);

        context =   this;
        mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(this);

        mBtnSend = findViewById(R.id.btn_send);
        mBtnDisconnect = findViewById(R.id.btn_disconnect);
        mBtnScan = findViewById(R.id.btn_scan);
        mBtnClear   =   findViewById(R.id.btn_clear);
        mBtnClose   =   findViewById(R.id.btn_close);
        mBtnOpen    =   findViewById(R.id.btn_open);
        mLvDeviceList = findViewById(R.id.lv_bluelist);
        mTextView   =   findViewById(R.id.tv_recv);
        mEditText   =   findViewById(R.id.et_send);
        setListener();

        /*
        * GPS COARSE LOCATION permission checked.
        *
        * */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
            }
        }

        /*
        *  Check bluetooth state.
        * */
        mBluetoothAdapter =   BluetoothAdapter.getDefaultAdapter();
        if( mBluetoothAdapter == null ) {
            Toast.makeText(this, "Bluetooth is not available.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if( !mBluetoothAdapter.isEnabled() ) {
            //Toast.makeText(this, "Please enable your Bluetooth and re-run this program.", Toast.LENGTH_LONG).show();
            mBtnOpen.setEnabled(true);
            mBtnClose.setEnabled(false);
            System.out.println("is enable");

        }else{
            mBtnOpen.setEnabled(false);
            mBtnClose.setEnabled(true);
            System.out.println("is not enable");
        }

        /*
        *  Find nearby bluetooth, get it address.
        * */
        BluetoothReceiver   mBlueToothReceiver = new BluetoothReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mIntentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBlueToothReceiver,mIntentFilter);
        System.out.println("the bluetooth device is register receiver...");


        /*
        * Add viewList click event. click to connect the remote bluetooth.
        * */
        mLvDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Click: "+position);

                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                try {
                    BluetoothDevice device = mBluetoothDeviceAdapter.getDevice(position);
                    Boolean returnValue = false;
                    Method createBondMethod;
                    if(device.getBondState() == BluetoothDevice.BOND_NONE) {
                        // 反射方法调用；
                        System.out.println("choose a band none device.");
                        createBondMethod = BluetoothDevice.class .getMethod("createBond");
                        System.out.println("开始配对");
                        returnValue = (Boolean) createBondMethod.invoke(device);
                        //mLeDeviceListAdapter_isConnect.notifyDataSetChanged();
                        Toast.makeText(BluetoothBleActivity.this, "点击设备是"+ device.getName() + "   " + device.getAddress(), Toast.LENGTH_LONG).show() ;
                    }else if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                        System.out.println("choose a band device!!");
                        connectThread = new ConnectThread(device, mBluetoothAdapter, mUIHandler);
                        connectThread.start();
                    }

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        });



        /*
        *  Register broadcast.
        * */
        /*
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.setPriority(Integer.MAX_VALUE);


        */

    }


    private void setListener( ) {
        OnClick onClick = new OnClick();

        mBtnOpen.setOnClickListener(onClick);
        mBtnSend.setOnClickListener(onClick);
        mBtnScan.setOnClickListener(onClick);
        mBtnClear.setOnClickListener(onClick);
        mBtnClose.setOnClickListener(onClick);
        mBtnDisconnect.setOnClickListener(onClick);

    }
    /*
    *  add
    * */
    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick( View v ) {
            Intent intent = null;
            switch( v.getId() ) {
                case R.id.btn_open:
                    mBluetoothAdapter.enable();
                    mBtnOpen.setEnabled(false);
                    mBtnClose.setEnabled(true);
                    break;
                case R.id.btn_close:
                    mBluetoothAdapter.disable();
                    mBtnOpen.setEnabled(true);
                    mBtnClose.setEnabled(false);
                    break;
                case R.id.btn_disconnect:
                    connectThread.cancel();
                    showToast("Bluetooth connection has been closed!");
                    mLvDeviceList.setBackgroundColor(0);
                    mLvDeviceList.setEnabled(true);
                    break;
                case R.id.btn_scan:
                    /*
                    * Clear the list items.
                    * */
                    mBluetoothDeviceAdapter.clear();
                    mBluetoothDeviceAdapter.notifyDataSetChanged();
                    /*
                    * If discovering so cancel discovery.
                    * */
                    if(mBluetoothAdapter.isDiscovering()){
                        mBluetoothAdapter.cancelDiscovery();
                    }

                    /*
                    * find other bluetooth device.
                    * */
                    mBluetoothAdapter.startDiscovery();
                    break;
                case R.id.btn_send:
                    String text = mEditText.getText().toString();
                    connectThread.sendData( text.getBytes() );
                    break;
                case R.id.btn_clear:
                    mTextView.setText("");
                    break;

            }

        }

    }

    public class BluetoothReceiver extends BroadcastReceiver {


        private String pair_info;
        private String unpair_info;
        private String state_info;


        @Override
        public void onReceive(Context context, Intent intent ) {

            String action = intent.getAction();
            System.out.println ( "SYSTEM: action triggered: " + action  );

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                mLvDeviceList.setAdapter(mBluetoothDeviceAdapter);
                System.out.println ( "SYSTEM: Find a device : " + device.getName() + " : " + device.getAddress()  );
                // Scanned a device add to List
                mBluetoothDeviceAdapter.addDevice(device);
                // 数据改变并更新列表
                mBluetoothDeviceAdapter.notifyDataSetChanged();
                if( device.getBondState() == BluetoothDevice.BOND_BONDED ) {
                    pair_info = device.getAddress();
                }else {
                    unpair_info = device.getAddress();
                }

            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                System.out.println ( "SYSTEM: Discovery finished..."  );
            }
        }

        public void set_pairInfo( String val ) {
            pair_info = val;
        }
        public String get_pairInfo() {
            return pair_info;
        }
        public void set_unpairInfo( String val ) {
            unpair_info = val;
        }
        public String get_unpairInfo() {
            return unpair_info;
        }
        public void set_stateInfo( String val ) {
            state_info = val;
        }
        public String get_stateInfo() {
            return state_info;
        }
    }
    private void showToast(String text) {

        if( mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        }
        else {
            mToast.setText(text);
        }
        mToast.show();
    }
    /**
     * 处理消息
     */
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_START_LISTENING:
                    setProgressBarIndeterminateVisibility(true);
                    System.out.println("Start to listener...");
                    break;
                case Constant.MSG_FINISH_LISTENING:
                    setProgressBarIndeterminateVisibility(false);
                    System.out.println("stop listenner");
                    break;
                case Constant.MSG_GOT_DATA:
                    mTextView.append(String.valueOf(msg.obj));
                    System.out.println("data: "+String.valueOf(msg.obj));
                    break;
                case Constant.MSG_ERROR:
                    System.out.println("error: "+String.valueOf(msg.obj));
                    break;
                case Constant.MSG_CONNECTED_TO_SERVER:
                    System.out.println("Connected to Server");
                    mLvDeviceList.setEnabled(false);
                    mLvDeviceList.setBackgroundColor(Color.rgb(119,136,153));
                    showToast("Bluetooth connection has been set up!");
                    break;
                case Constant.MSG_GOT_A_CLINET:
                    System.out.println("Got a Client");
                    break;
            }
        }
    }

}
