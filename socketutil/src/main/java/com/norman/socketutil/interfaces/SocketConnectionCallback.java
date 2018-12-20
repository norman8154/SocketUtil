package com.norman.socketutil.interfaces;

/**
 * Created by Norman on 2018/6/7.
 */

public interface SocketConnectionCallback {
    void connected();
    void disconnected();
    void connectFailed();
    void alreadyConnected();
}
