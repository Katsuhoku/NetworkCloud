package model;

import java.net.Socket;

import org.json.JSONObject;

public class RemoteSender extends Thread {
    private String remoteNodeName;
    private String remoteAddress;
    private int remotePort;

    // Communication Socket
    private Socket receiver;

    // Subordinated Queue
    private Queue subQueue;

    public RemoteSender(CloudCore core, JSONObject remoteNode) {
        remoteNodeName = remoteNode.getString("name");
        remoteAddress = remoteNode.getString("address");
        remotePort = remoteNode.getInt("port");
    }

    @Override
    public void run() {

    }

    // Receive an Operation and append it to this RemoteSender's Subordinated Queue
    public void addOperation(Operation op) {

    }
}