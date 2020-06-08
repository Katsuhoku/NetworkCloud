package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
    Class: ConnectionPoint
    Description: The Server where remote nodes will connect to send (to this node)
    messages and data. Is only listening for connections; when one arrives, creates
    a RemoteReceiver thread, passing the created socket.
*/

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
                core.addRemoteReceiver(new RemoteReceiver(core, socket));
            }
        } catch (IOException e) {
            System.out.println("Error?");
        }
        
        
    }
}