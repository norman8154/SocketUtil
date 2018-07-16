package com.norman.socketutil;

import android.icu.util.Output;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Norman on 2018/7/16.
 */

public class SocketEntity {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private BufferedReader br;

    public SocketEntity(Socket socket, InputStream is, OutputStream os, BufferedReader br) {
        this.socket = socket;
        this.is = is;
        this.os = os;
        this.br = br;
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInputStream() {
        return is;
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public BufferedReader getBufferedReader() {
        return br;
    }
}
