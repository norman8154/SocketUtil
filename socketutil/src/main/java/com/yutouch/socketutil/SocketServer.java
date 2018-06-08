package com.yutouch.socketutil;

import android.util.Log;

import com.yutouch.socketutil.interfaces.OnReceiveMessageListener;
import com.yutouch.socketutil.interfaces.SendMessageCallback;
import com.yutouch.socketutil.interfaces.SocketConnectionCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Norman on 2018/6/7.
 */

public class SocketServer {
    private ServerSocket socketServer;
    private Socket client;
    private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    private SocketConnectionCallback connectionCallback;
    private OnReceiveMessageListener onReceiveMessageListener;

    public void setConnectionCallback(SocketConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    public void setOnReceiveMessageListener(OnReceiveMessageListener onReceiveMessageListener) {
        this.onReceiveMessageListener = onReceiveMessageListener;
    }

    public void sendMessage(final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (client != null) {
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
                if (client != null) {
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
        if(client != null) {
            try {
                client.close();
                client = null;
                is.close();
                os.close();
                br.close();

                if (connectionCallback != null)
                    connectionCallback.disconnected();

                waitConnection();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else
            return false;
    }

    public boolean closeServer() {
        if (socketServer != null){
            try {
                socketServer.close();
                socketServer = null;

                if (client != null) {
                    client.close();
                    client = null;
                    is.close();
                    os.close();
                    br.close();
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else
            return false;
    }

    public boolean startServer(int port) {
        if (socketServer == null) {
            try {
                socketServer = new ServerSocket(port);
                waitConnection();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else
            return false;
    }

    private void waitConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = socketServer.accept();

                    is = client.getInputStream();
                    os = client.getOutputStream();
                    br = new BufferedReader(new InputStreamReader(is));

                    if (connectionCallback != null)
                        connectionCallback.connected();

                    SocketListenThread socketListenThread = new SocketListenThread(client, br);
                    socketListenThread.setOnReceiveMessageListener(onReceiveMessageListener);
                    socketListenThread.start();

                    socketListenThread.join();
                    Log.d("Socket Server", "Connection end");

                    disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
