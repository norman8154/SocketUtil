package com.norman.socketutil.SocketClient;

import android.os.Handler;
import android.util.Log;

import com.norman.socketutil.interfaces.SendMessageCallback;
import com.norman.socketutil.interfaces.SocketConnectionCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Norman on 2018/6/7.
 */

public class SocketClient {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    private SocketConnectionCallback connectionCallback;
    private OnReceiveMessageListener onReceiveMessageListener;
    private Handler handler;

    public SocketClient() {
        this.handler = new Handler();
    }

    public void setConnectionCallback(SocketConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    public void setOnReceiveMessageListener(OnReceiveMessageListener onReceiveMessageListener) {
        this.onReceiveMessageListener = onReceiveMessageListener;
    }

    public void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket != null) {
                    try {
                        os.write(message.getBytes());
                        os.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void sendMessage(final String message, final SendMessageCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket != null) {
                    try {
                        os.write(message.getBytes());
                        os.flush();

                        if (callback != null)
                            callback.sendSuccess();
                    } catch (IOException e) {
                        e.printStackTrace();

                        if (callback != null)
                            callback.sendFailed();
                    }
                } else if (callback != null)
                    callback.sendFailed();
            }
        }).start();
    }

    public boolean disconnect(){
        if (socket != null && socket.isConnected()) {
            try {
                sendMessage("{\"Action\":\"Close\"}");
                socket.close();
                socket = null;
                is.close();
                os.close();
                br.close();

                if(connectionCallback != null)
                    connectionCallback.disconnected();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else
            return false;
    }

    public void makeConnection(final String ip, final int port) {
        if (socket == null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket(ip, port);

                        is = socket.getInputStream();
                        os = socket.getOutputStream();
                        br = new BufferedReader(new InputStreamReader(is));

                        if (connectionCallback != null)
                            connectionCallback.connected();

                        SocketListenThread socketListenThread = new SocketListenThread(socket, br);
                        socketListenThread.setOnReceiveMessageListener(onReceiveMessageListener);
                        socketListenThread.start();

                        socketListenThread.join();
                        Log.d("Socket Client", "Connection end");

                        disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();

                        if (connectionCallback != null)
                            connectionCallback.disconnected();
                    }
                }
            }).start();
        else if (connectionCallback != null)
            connectionCallback.alreadyConnected();
    }
}
