package model;

import org.json.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import controller.Controller;

/**
 * The core <code>Thread</code> of the system. CloudCore manages the {@link model.Queue 
 * Master Operation Queue}, delegate remote operations to the {@link model.Queue 
 * Subordinated Operation Queues} and do all local operations.
 * Also manages all files an {@link model.FileTable FileTables}.
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
    private HashMap<String, RemoteSender> remoteSenderThreads;
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

        remoteReceiverThreads = new ArrayList<>();
        remoteSenderThreads = new HashMap<>();
    }

    @Override
    public void run() {
        // Establish all configuration parameters
        name = config.getString("name");
        systemDirectory = config.getString("path");
        remoteNodes = (JSONArray) config.get("remote");
        backupNode = (JSONObject) config.get("backup");

        // Initializes the Master Queue
        initMasterQueue();

        // Starts all other system sections
        initConnectionPoint();
        initRemoteConnections();
        initBackup();
        initBackupPoint();
    }

    private void initMasterQueue() {
        masterQueue = new Queue(systemDirectory + "/sysfiles/queues/master.q");
    }

    // Initialize ConnectionPoint thread to start server and receive connections
    // from other nodes
    private void initConnectionPoint() {
        connectionPointThread = new ConnectionPoint(this, config.getInt("localport"));
        connectionPointThread.start();
    }

    // Initialize all RemoteSenders threads to connect to remote ConnectionPoints
    private void initRemoteConnections() {
        remoteNodes = config.getJSONArray("remote");

        for (int i = 0; i < remoteNodes.length(); i++) {
            JSONObject remoteNode = remoteNodes.getJSONObject(i);
            RemoteSender aux = new RemoteSender(this, remoteNode);
            aux.start();

            remoteSenderThreads.put(remoteNode.getString("name"), aux);
        }
    }

    // Initalize BackupAdmin thread to manage this node's backup
    private void initBackup() {

    }

    // Intialize BackupSlave thread to receive files to backup from a remote node
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
     * Search and returns the requested {@link model.FileTable FileTable}.
     * @param path the location of the {@link model.FileTable FileTable}.
     * @return a copy of the FileTable if found, or <code>null</code> if not.
     */
    public FileTable getFileTable(String path) {
        return null; // ****
    }

    /**
     * Saves the passed <code>File</code> in the specified path. (?)
     * <h2>(¿Cómo era que se guardaban archivos en Java? xD)</h2>
     * @param path the path (filename included) where the file will be saved.
     * @param file the file data. <b>(???)
     */
    public void saveFile(String path, File file) {

    }

    /**
     * Creates a new directory in the specified path.
     * @param path the path (directory name included) where the directory will be
     * created.
     */
    public void createDirectory(String path) {

    }

    /**
     * Removes from the local OS file system the specified file or directory.
     * @param path the path (file or directory name included) where is located the
     * file/directory to delete.
     */
    public void delete(String path) {

    }

    /**
     * <h2>(¿Cómo era que se guardaban archivos en Java? x2)</h2>
     * @param path
     * @return
     */
    public File getFile(String path) {
        return null; // ****
    }

    /**
     * @return the path on the local OS file system where are located the {@link
     * model.Queue Operation Queues}.
     */
    public String getSystemQueuesDirectory() {
        return systemDirectory + "/sysfiles/queues";
    }

}