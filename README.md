# SocketUtil

Step 1. Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.norman8154:SocketUtil:1.0.1'
	}
  
  
  -------
  
# Usage

Server side
  
    SocketServer server = new SocketServer();
    server.startServer(port_number);
    
Client side
  
    SocketClient client = new SocketClient();
    client.makeConnection(ip_address, port_number);
