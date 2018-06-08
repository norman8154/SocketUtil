package com.yutouch.socketutil;

import android.util.Log;

import com.yutouch.socketutil.interfaces.OnReceiveMessageListener;

import java.io.BufferedReader;
import java.net.Socket;

/**
 * Created by Norman on 2018/6/7.
 */

class SocketListenThread extends Thread {
    private Socket socket;
    private BufferedReader br;
    private OnReceiveMessageListener onReceiveMessageListener;

    public SocketListenThread(Socket socket, BufferedReader br) {
        this.socket = socket;
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

                switch (message){
                    case "close":
                        socket.close();
                        break;

                    default:
                        onReceiveMessageListener.receiveMessage(message);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
