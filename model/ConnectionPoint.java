package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The <code>Thread</code> Server where remote nodes will connect to send (to 
 * this node) messages and data. Is only listening for connections; when one arrives, 
 * creates a {@link model.RemoteReceiver RemoteReceiver} <code>Thread</code>, passing 
 * the created <code>Socket</code>.
 */

public class ConnectionPoint extends Thread {
    /**
     * The system {@link model.CloudCore core}.
     */
    private CloudCore core;

    /**
     * The port of the <code>ServerSocket</code>
     */
    private int localPort;

    /**
     * Creates an instance of this thread to manage incoming connections.
     * @param core this system {@link model.CloudCOore core}.
     * @param localPort the local port wich the <code>ServerSocket</code> will be binded.
     */
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
                core.addRemoteReceiver(new RemoteReceiver(core, socket, core.getRemoteNodeName(socket.getInetAddress().getHostAddress(), socket.getPort())));
            }
        } catch (IOException e) {
            System.out.println("Error?");
        }
    }
}