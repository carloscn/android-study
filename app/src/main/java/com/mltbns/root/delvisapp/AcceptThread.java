package com.mltbns.root.delvisapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * 服务端处理请求的线程
 */
public class AcceptThread extends Thread {
    //连接的服务名称
    private static final String NAME = "BlueToothClass";
    //通信的UUID,注意，这个UUID是蓝牙通信的UUID,必须是这个UUID,不能使随便创建的UUID
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTTION_UUID);
    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;
    //连接成功后，处理请求的线程
    private ConnectedThread mConnectedThread;

    public AcceptThread(BluetoothAdapter adapter, Handler handler) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        mBluetoothAdapter = adapter;
        mHandler = handler;
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            //获取一个服务端Socket对象，第一个参数为服务名称，
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // 保持监听直到异常发生或者返回了一个Socket
        while (true) {
            try {
                //开始监听
                mHandler.sendEmptyMessage(Constant.MSG_START_LISTENING);
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                //发送异常的信息
                mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR, e));
                break;
            }
            // 如果接受到了一个连接，就会获得一个socket对象
            if (socket != null) {
                // 处理连接，在单独的线程中
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                    mHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        //为了简化，只支持同时处理一个连接，实际项目中可能会有多个连接，就需要连接池了
        if( mConnectedThread != null) {
            mConnectedThread.cancel();
        }
        mHandler.sendEmptyMessage(Constant.MSG_GOT_A_CLINET);
        mConnectedThread = new ConnectedThread(socket, mHandler);
        mConnectedThread.start();
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
            mHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
        } catch (IOException e) { }
    }

    public void sendData(byte[] data) {
        if( mConnectedThread!=null){
            mConnectedThread.write(data);
        }
    }
}