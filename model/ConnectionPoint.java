package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionPoint extends Thread {
    private CloudCore core;
    private int localPort;

    public ConnectionPoint(CloudCore core, int localPort) {
        this.core = core;
        this.localPort = localPort;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(localPort);

            while (true){
                //This socket will be specifically used to receive data
                Socket socket = serverSocket.accept();
                socket.shutdownOutput();

                // Send socket to RemoteReceiver
                core.addRemoteReceiver(new RemoteReceiver(socket));
            }
        } catch (IOException e) {
            System.out.println("Error?");
        }
        
        
    }
}