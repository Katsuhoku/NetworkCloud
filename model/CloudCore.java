package model;

import java.util.ArrayList;

import org.json.*;
import controller.Controller;

/*
    Class: CloudCore
    Description: The core of the system. It manages the Master Operation Queue, 
    delegate remote operations to the Subordinated Operation Queues and do all
    local operations.
    Also manages all files and File Tables.
*/

public class CloudCore extends Thread {
    private Controller controller;
    private JSONObject config;
    
    // Node information
    private String name;
    private String systemDirectory;
    private JSONArray remoteNodes;
    private JSONObject backupNode;

    // Threads
    private ConnectionPoint connectionPointThread;
    private ArrayList<RemoteSender> remoteSenderThreads;
    private ArrayList<RemoteReceiver> remoteReceiverThreads;
    private BackupAdmin backupAdminThread;
    private BackupSlave backupSlaveThread;

    // Master Queue
    private Queue masterQueue;

    // File Tables Root
    private FileTable root;

    public CloudCore(Controller controller, JSONObject config) {
        this.controller = controller;
        this.config = config;
    }

    @Override
    public void run()  {
        // Establish all configuration parameters
        name = config.getString("name");
        systemDirectory = config.getString("path");
        remoteNodes = (JSONArray) config.get("remote");
        backupNode = (JSONObject) config.get("backup");

        // Starts all other system sections
        initConnectionPoint();
        initRemoteConnections();
        initBackup();
        initBackupPoint();
    }

    // Initialize ConnectionPoint thread to start server and receive connections from other nodes
    private void initConnectionPoint() {
        connectionPointThread = new ConnectionPoint(config.getInt("localport"));
        connectionPointThread.start();
    }

    // Initialize all RemoteSenders threads to connect to remote ConnectionPoints
    private void initRemoteConnections() {

    }

    // Initalize BackupAdmin thread to manage this node's backup
    private void initBackup() {

    }

    // Intialize BackupSlave thread to receive files to backup from a remote node
    private void initBackupPoint() {

    }

    // Add RemoteReceiver thread to the list. This method is called by the ConnectionPoint, wich must be
    // previoulsy created
    public void addRemoteReceiver(RemoteReceiver thread) {

    }
}