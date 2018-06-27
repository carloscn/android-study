package com.mltbns.root.delvisapp;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Rex on 2015/5/30.
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mHandler = handler;
        // 获取输入输出流
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes;
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // 从输入流读取数据
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                if( bytes >0) {
                    Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA, new String(buffer, 0, bytes, "utf-8"));
                    mHandler.sendMessage(message);
                }
                Log.d("GOTMSG", "message size" + bytes);
            } catch (IOException e) {
                mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR, e));
                break;
            }
        }
    }
    /* 发送数据岛远程设备*/
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
    /* 关闭连接 */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}