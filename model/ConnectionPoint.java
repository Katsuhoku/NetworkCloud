package model;

import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionPoint extends Thread {
    private CloudCore core;
    private int localPort;
    

    public ConnectionPoint(CloudCore core, int localPort) {
        this.core = core;
        this.localPort = localPort;
    }

    //
    @Override
    public void run() {
        ServerSocket serverSocket = new ServerSocket(localPort);
        
        while (true){
            Socket socket = serverSocket.accept();
            this.core.addRemoteReceiver(new RemoteReceiver());
        }
        
    }
}