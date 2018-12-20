package com.norman.socketutil.SocketServer;

import android.util.Log;

import com.norman.socketutil.SocketEntity;
import com.norman.socketutil.SocketServer.SocketListenThread;
import com.norman.socketutil.SocketServer.OnReceiveMessageListener;
import com.norman.socketutil.interfaces.SocketConnectionCallback;
import com.norman.socketutil.interfaces.SendMessageCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Norman on 2018/6/7.
 */

public class SocketServer {
    private ServerSocket socketServer;
    private ArrayList<SocketEntity> clients = new ArrayList<>();
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
                if (clients.size() > 0) {
                    for (SocketEntity client : clients) {
                        try {
                            client.getOutputStream().write(message.getBytes());
                            client.getOutputStream().flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void sendMessage(final String message, final SendMessageCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (clients.size() > 0) {
                    for (SocketEntity client : clients) {
                        try {
                            client.getOutputStream().write(message.getBytes());
                            client.getOutputStream().flush();

                            if (callback != null)
                                callback.sendSuccess();
                        } catch (IOException e) {
                            e.printStackTrace();

                            if (callback != null)
                                callback.sendFailed();
                        }
                    }
                } else if (callback != null)
                    callback.sendFailed();
            }
        }).start();
    }

    public void sendMessage(final SocketEntity client, final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (client != null) {
                    try {
                        client.getOutputStream().write(message.getBytes());
                        client.getOutputStream().flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void sendMessage(final SocketEntity client, final String message, final SendMessageCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (client != null) {
                    try {
                        client.getOutputStream().write(message.getBytes());
                        client.getOutputStream().flush();

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

    public boolean disconnect(SocketEntity entity){
        if(entity != null) {
            try {
                entity.getSocket().close();
                entity.getInputStream().close();
                entity.getOutputStream().close();
                entity.getBufferedReader().close();

                if (connectionCallback != null)
                    connectionCallback.disconnected();

                //waitConnection();

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

                for (SocketEntity client : clients) {
                    if (client != null) {
                        client.getSocket().close();
                        client.getInputStream().close();
                        client.getOutputStream().close();
                        client.getBufferedReader().close();
                    }
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

                if (connectionCallback != null)
                    connectionCallback.connectFailed();

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
                    Socket client = socketServer.accept();
                    InputStream is = client.getInputStream();
                    OutputStream os = client.getOutputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    SocketEntity entity = new SocketEntity(client, is, os, br);

                    clients.add(entity);

                    Log.d("Client Accepted", "Total client " + clients.size());

                    if (connectionCallback != null)
                        connectionCallback.connected();

                    waitConnection();

                    SocketListenThread socketListenThread = new SocketListenThread(entity, br);
                    socketListenThread.setOnReceiveMessageListener(onReceiveMessageListener);
                    socketListenThread.start();

                    socketListenThread.join();
                    Log.d("Socket Server", "Connection end");

                    clients.remove(entity);
                    disconnect(entity);
                } catch (Exception e) {
                    e.printStackTrace();

                    if (connectionCallback != null)
                        connectionCallback.disconnected();
                }
            }
        }).start();
    }
}
