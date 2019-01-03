package com.norman.socketutil.SocketServer;

import android.util.Log;

import com.norman.socketutil.SocketEntity;
import com.norman.socketutil.SocketServer.OnReceiveMessageListener;

import java.io.BufferedReader;
import java.net.Socket;

/**
 * Created by Norman on 2018/6/7.
 */

public class SocketListenThread extends Thread {
    private SocketEntity client;
    private Socket socket;
    private BufferedReader br;
    private OnReceiveMessageListener onReceiveMessageListener;

    public SocketListenThread(SocketEntity client, BufferedReader br) {
        this.client = client;
        this.socket = client.getSocket();
        this.br = br;
    }

    public void setOnReceiveMessageListener(OnReceiveMessageListener onReceiveMessageListener) {
        this.onReceiveMessageListener = onReceiveMessageListener;
    }

    @Override
    public void run() {
        super.run();

        Log.d("Socket Server", "Start listening...");
        while (!socket.isClosed() && socket.isConnected()){
            try {
                final String message = br.readLine();
                Log.d("Socket service", "Receive: " + message);

                if (message != null) {
                    switch (message){
                        case "close":
                            socket.close();
                            break;

                        default:
                            onReceiveMessageListener.receiveMessage(client, message);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
