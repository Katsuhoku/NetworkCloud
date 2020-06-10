package model;

import org.json.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import controller.Controller;
import model.Operation.Status;
import model.Operation.Type;

/**
 * The core <code>Thread</code> of the system. CloudCore manages the
 * {@link model.Queue Master Operation Queue}, delegate remote operations to the
 * {@link model.Queue Subordinated Operation Queues} and do all local
 * operations. (FileTable class is no longer needed)
 */

public class CloudCore extends Thread {
    /**
     * {@link controller.Controller Controller} of the application. Connects the
     * user interface with this core.
     */
    private Controller controller;

    /**
     * The configurable parameters, specified in the <code>config.json</code> file.
     */
    private JSONObject config;

    /**
     * This node name.
     */
    private String name;

    /**
     * The path where the system will save all the files. System will not access
     * further shallow than this directory. Only can go deeper as new directores are
     * created.
     */
    private String systemDirectory;

    /**
     * The remote nodes information.
     */
    private JSONArray remoteNodes;

    /**
     * The Backup node information.
     */
    private JSONObject backupNode;

    /**
     * This node's {@link model.ConnectionPoint ConnectionPoint} thread.
     */
    private ConnectionPoint connectionPointThread;

    /**
     * This node's {@link model.RemoteSender RemoteSender} threads. The key for
     * accessing a specific one is the remote node's name, specified in
     * <code>remoteNodes</code>.
     */
    private HashMap<String, RemoteSender> remoteSenderThreads;

    /**
     * This node's {@link model.RemoteReceiver RemoteReceiver} threads.
     */
    private ArrayList<RemoteReceiver> remoteReceiverThreads;

    /**
     * This node's {@link model.BackupAdmin BackupAdmin} thread.
     */
    private BackupAdmin backupAdminThread;

    /**
     * This node's {@link model.BackupSlave BackupSlave} thread.
     */
    private BackupSlave backupSlaveThread;

    /**
     * This node's {@link model.Queue Master Queue}.
     */
    private Queue masterQueue;

    /**
     * This node's root directory. It gets constructed using the <code>
     * systemDirectory</code> path.
     */
    private File root;

    public CloudCore(Controller controller, JSONObject config) {
        this.controller = controller;
        this.config = config;

        remoteReceiverThreads = new ArrayList<>();
        remoteSenderThreads = new HashMap<>();
    }

    @Override
    public void run() {
        // Establishing all configuration parameters
        name = config.getString("name");
        systemDirectory = config.getString("path");
        remoteNodes = (JSONArray) config.get("remote");
        backupNode = (JSONObject) config.get("backup");

        root = new File(systemDirectory + "/root");

        // Initializes the Master Queue
        initMasterQueue();

        // Starts all other system sections
        // initConnectionPoint();
        // initRemoteConnections();
        // initBackup();
        // initBackupPoint();

        // Starts the system
        while (true) {
            try {
                Operation next = masterQueue.getNext();

                if (next != null) {
                    System.out.println(next);
                    
                    switch (next.getType()) {
                        case TRANSFER:
                            // Obtain the sender node involved
                            // Pass the operation to the corresponding RemoteSender
                            // Note: An operation transfer will never has the sender node as the local node.
                            // Transfer operation means that this node's user is requesting to send a file
                            // from a remote node, to a wether other remote node or this local node.
                            // RemoteReceiver will receive the message for a transfer, and charge to the master
                            // queue a Send operation, which means the file to send is in this local node.
                            break;
                        case SEND:
                            // Pass the operation to the corresponding RemoteSender
                            break;
                        case DELETE:
                            // Gets the node involved
                            // If remote, pass this operation to the corresponding RemoteSender
                            // Else, Gets the path and tries to delete the file or directory
                            // Semaphore needed!
                            break;
                        case MKDIR:
                            // Gets the node involved
                            // If remote, pass this operation to the corresponding RemoteSender
                            // Gets the path and tries to create the directory
                            // Semaphore needed!
                            break;
                        case LISTDIR:
                            // Gets the node involved.
                            // If remote, then gets the status
                            // If is already confirmed, then takes desired dir content from the msg variable.
                            // Else (not confirmed), then it means the dir content wasn't solicited yet. Pass 
                            // this operation to the corresponding RemoteSender, and will list content
                            // when received. The core will not wait for that.
                            // Else (not remote), gets the content of the local dir
                            break;
                        case CONFIRM:
                            // Get the node involved
                            // If remote, pass this operation to the corresponding RemoteSender
                            // Else, search in the operation history (not defined yet) and confirms.
                            break;
                        case FAIL:
                            // Get the node involved
                            // If remote, pass this operation to the corresponding RemoteSender
                            // Else, search in the operation history (not defined yet) and marks as failed.
                            break;
                    }
                    sleep(3000);
                }
                else {
                    sleep(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes the Master {@link model.Operation Queue}.
     */
    private void initMasterQueue() {
        masterQueue = new Queue(systemDirectory + "/sysfiles/queues/master.q");
    }

    /**
     * Initializes the {@link model.ConnectionPoint ConnectionPoint} thread.
     */
    private void initConnectionPoint() {
        connectionPointThread = new ConnectionPoint(this, config.getInt("localport"));
        connectionPointThread.start();
    }

    /**
     * Initializes all {@link model.RemoteSender RemoteSender} threads.
     */
    private void initRemoteConnections() {
        remoteNodes = config.getJSONArray("remote");

        for (int i = 0; i < remoteNodes.length(); i++) {
            JSONObject remoteNode = remoteNodes.getJSONObject(i);
            RemoteSender aux = new RemoteSender(this, remoteNode);
            aux.start();

            remoteSenderThreads.put(remoteNode.getString("name"), aux);
        }
    }

    /**
     * Initializes the {@link model.BackupAdmin BackupAdmin} thread.
     */
    private void initBackup() {

    }

    /**
     * Initializes the {@link model.BackupSlave BackupSlave} thread.
     */
    private void initBackupPoint() {

    }

    /**
     * Add a new {@link model.RemoteReceiver} thread to the list. This method has
     * to be called only by the {@link model.ConnectionPoint ConnectionPoint} thread,
     * wich must be previously created.
     * @param thread the new instance of {@link model.RemoteReceiver RemoteReceiver}.
     */
    public void addRemoteReceiver(RemoteReceiver thread) {
        thread.start();
        remoteReceiverThreads.add(thread);
    }

    /**
     * Appends the specified {@link model.Operation Operation} in the {@link model.Queue 
     * Master Queue}.
     * @param op the {@link model.Operation Operation} instance to add.
     */
    public void addOperation(Operation op) {
        try {
            masterQueue.add(op);
        } catch (IOException e) {
            System.out.println("Fatal Error: Cannot access to Master Queue");
        } catch (InterruptedException ie) {
            System.out.println("Error: Current thread has stopped while accessing Master Queue.");
            System.out.println("More details:\n");
            ie.printStackTrace();
        }
    }

    /**
     * Creates a new directory in the specified path.
     * @param path the <code>systemDirectory root</code> relative path (directory name
     * included) where the directory will be created.
     * @return <code>true</code> if the directory was created. <code>false</code>
     * otherwise.
     */
    public boolean createDirectory(String path) {
        return new File(root.getAbsolutePath() + "/" + path).mkdir();
    }

    /**
     * Removes from the local OS file system the specified file or directory.
     * @param path the <code>systemDirectory root</code> relative path (file or directory 
     * name included) where is located the file/directory to delete.
     * @return <code>true</code> if and only if the file or directory is successfully 
     * deleted; <code>false</code> otherwise.
     */
    public boolean delete(String path) {
        return new File(root.getAbsolutePath() + "/" +path).delete();
    }

    /**
     * @return the path on the local OS file system where are located the {@link
     * model.Queue Operation Queues}.
     */
    public String getSystemQueuesDirectory() {
        return systemDirectory + "/sysfiles/queues";
    }

    /**
     * @return the absolute path where incoming files will be saved.
     */
    public String getReceivedFilesDirectory() {
        return root.getAbsolutePath() + "recv";
    }

}