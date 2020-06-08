package model;

import java.net.Socket;

/*
    Class: RemoteReceiver
    Description: Thread for receive messages and data from remote nodes. It's created
    when the ConnectionPoint server recevies an incoming connection. If the remote
    node this thread is managing gets disconnected, then this thread is interrumpted.
    A new one will created when the remote node gets connected again.
    When receives a message, this thread manages to put the corresponding operation in
    the Master Queue.
*/

public class RemoteReceiver extends Thread {
    private CloudCore core;
    private Socket receiver;

    public RemoteReceiver(CloudCore core, Socket receiver){
        this.core = core;
        this.receiver = receiver;
    }
}