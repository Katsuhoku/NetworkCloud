package model;

import java.net.Socket;

/**
 * RemoteReceiver is a <code>Thread</code> for receive messages an data from remote
 * nodes. It's created when the {@link model.ConnectionPoint ConnectionPoint}
 * server receives an incomming connection.
 * If the remote node this thread is managing gets disconnected, then this thread
 * is interrumpted. A new one will be created when the remote node gets connected again.
 * When receives a message, this thread manages to put the corresponding operation in
 * the {@link model.Queue Master Queue}.
 */

public class RemoteReceiver extends Thread {
    // System core
    private CloudCore core;
    
    // Communication Socket
    private Socket receiver;

    /**
     * Creates an instance of this thread to manage incoming messages and data.
     * @param core this system {@link model.CloudCore core}.
     * @param receiver the socket created by the {@link model.ConnectionPoint ConnectionPoint}.
     */
    public RemoteReceiver(CloudCore core, Socket receiver){
        this.core = core;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        
    }
}