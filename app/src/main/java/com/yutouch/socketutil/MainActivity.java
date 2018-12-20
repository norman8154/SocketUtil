package com.yutouch.socketutil;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.norman.socketutil.SocketClient.SocketClient;
import com.norman.socketutil.SocketEntity;
import com.norman.socketutil.SocketServer.SocketServer;
import com.norman.socketutil.SocketClient.OnReceiveMessageListener;
import com.norman.socketutil.interfaces.SocketConnectionCallback;

public class MainActivity extends AppCompatActivity {
    private EditText edtIP;
    private Button btnStartServer, btnCloseServer, btnStartClient, btnCloseClient, btnSendMessage;
    private SocketServer socketServer;
    private SocketClient socketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtIP = (EditText) findViewById(R.id.edt_ip);
        btnStartServer = (Button) findViewById(R.id.btn_start_server);
        btnCloseServer = (Button) findViewById(R.id.btn_close_server);
        btnStartClient = (Button) findViewById(R.id.btn_start_client);
        btnCloseClient = (Button) findViewById(R.id.btn_close_client);
        btnSendMessage = (Button) findViewById(R.id.btn_send_message);

        socketServer = new SocketServer();
        socketClient = new SocketClient();
        getMyIp();

        btnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketServer.startServer(8254);
                socketServer.setOnReceiveMessageListener(new com.norman.socketutil.SocketServer.OnReceiveMessageListener() {
                    @Override
                    public void receiveMessage(SocketEntity entity, String message) {
                        Log.d("Receive", message);
                    }
                });

                socketServer.setConnectionCallback(new SocketConnectionCallback() {
                    @Override
                    public void connected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Connected to client", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void disconnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Disconnected to client", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void connectFailed() {

                    }

                    @Override
                    public void alreadyConnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Already connected to client", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        btnCloseServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketServer.closeServer();
            }
        });

        btnStartClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketClient.makeConnection(edtIP.getText().toString(), 8254);

                socketClient.setOnReceiveMessageListener(new OnReceiveMessageListener() {
                    @Override
                    public void receiveMessage(String message) {
                        Log.d("Receive", message);
                    }
                });

                socketClient.setConnectionCallback(new SocketConnectionCallback() {
                    @Override
                    public void connected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Connected to server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void disconnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Disconnected to server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void connectFailed() {

                    }

                    @Override
                    public void alreadyConnected() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Already connected to server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        btnCloseClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketClient.disconnect();
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketClient.sendMessage("123");
            }
        });
    }

    private String getMyIp(){
        WifiManager wifi_service = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = String.format("%d.%d.%d.%d",(ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        edtIP.setText(ip);

        return ip;
    }

    @Override
    protected void onDestroy() {
        socketClient.disconnect();
        socketServer.closeServer();

        super.onDestroy();
    }
}
