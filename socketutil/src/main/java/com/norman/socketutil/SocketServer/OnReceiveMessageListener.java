package com.norman.socketutil.SocketServer;

import com.norman.socketutil.SocketEntity;

public interface OnReceiveMessageListener {
    void receiveMessage(SocketEntity entity, String message);
}
