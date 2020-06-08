package model;

import java.net.Socket;
import org.json.JSONObject;

/*
    Class: RemoteSender
    Description: Thread for send messages and data to remote nodes. It's created by
    the system core at the start. If the remote node this thread is managing gets 
    disconnected, then it will try to reconnect until it gets connected again.
    It receives Operations from the core (in its Subordinated Queue), and manages
    the corresponding message.
*/

public class RemoteSender extends Thread {
    // System core
    private CloudCore core;

    // Remote Node Info
    private String remoteNodeName;
    private String remoteAddress;
    private int remotePort;

    // Communication Socket
    private Socket receiver;

    // Subordinated Queue
    private Queue subQueue;

    public RemoteSender(CloudCore core, JSONObject remoteNode) {
        this.core = core;

        remoteNodeName = remoteNode.getString("name");
        remoteAddress = remoteNode.getString("address");
        remotePort = remoteNode.getInt("port");
    }

    @Override
    public void run() {
        // Initialize this subordinated queue
        initQueue();
    }

    // Configurates this RemoteSender's Subordinated Queue
    private void initQueue() {
        FileHandler queueFile = new FileHandler();
        String queueFileName = remoteNodeName + ".q";
        queueFile.open(core.getSystemQueuesDirectory() + "/" + queueFileName, "w");
        subQueue = new Queue(core.getSystemQueuesDirectory() + "/" + queueFileName);
        // ** Actually first has to ask if already exists **
    }

    // Receive an Operation and append it to this RemoteSender's Subordinated Queue
    public void addOperation(Operation op) {

    }
}