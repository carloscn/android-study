package com.mltbns.root.delvisapp;

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
import android.icu.text.SymbolTable;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;




public class BluetoothBleActivity extends AppCompatActivity {

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

    private List<BluetoothDevice> mBlueList = new ArrayList<>();
    private Context context;


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



        mLvDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Click: "+position);

                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
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

                    break;
                case R.id.btn_scan:
                    String s_info;
                    if(mBluetoothAdapter.isDiscovering()){
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    mBluetoothAdapter.startDiscovery();
                    break;
                case R.id.btn_send:

                    break;
                case R.id.btn_clear:

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
                // 扫描到设备则添加到适配器
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







}
