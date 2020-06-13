package model;

import org.json.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import controller.Controller;

/**
 * The core <code>Thread</code> of the system. CloudCore manages the
 * {@link model.Queue Master Operation Queue}, delegate remote operations to the
 * {@link model.Queue Subordinated Operation Queues} and do all local
 * operations. (FileTable class is no longer needed)
 * <p>
 * <b>Katsushika/2020/06/11:</b> {@link model.Operation Operation} History is no
 * longer needed due to the elimination of Operation replies. Also, the cases
 * <code>CONFIRM</code> and <code>FAIL</code> were removed.
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

    /**
     * <code>DELETE - SEND</code> <b>Synchronization</b>
     * <p>
     * Currently sending data RemoteSender threads count.
     */
    private int sendingCount;

    /**
     * <code>DELETE - SEND</code> <b>Synchronization</b>
     * <p>
     * Semaphores for Readers/Writers problem.
     */
    private Semaphore x, y, z, del, send;

    /**
     * Incoming files and dir data mutex.
     */
    private Semaphore recv;

    public CloudCore(Controller controller, JSONObject config) {
        this.controller = controller;
        this.config = config;

        remoteReceiverThreads = new ArrayList<>();
        remoteSenderThreads = new HashMap<>();

        sendingCount = 0;
        x = new Semaphore(1, true);
        y = new Semaphore(1, true);
        z = new Semaphore(1, true);
        del = new Semaphore(1, true);
        send = new Semaphore(1, true);

        recv = new Semaphore(1, true);
    }

    @Override
    public void run() {
        // Establishing all configuration parameters
        name = config.getString("name");
        systemDirectory = config.getString("path");
        remoteNodes = config.getJSONArray("remote");
        backupNode = config.getJSONObject("backup");

        // Initializes the system directories
        initSystemDir();

        root = new File(systemDirectory + "/root");

        // Initializes the Master Queue
        initMasterQueue();

        // Starts all other system sections
        // initConnectionPoint();
        // initRemoteConnections();
        // initBackup();
        // initBackupPoint();

        /*  NOTIFY GUI SYSTEM IS READY TO USE   */

        // System core process
        while (true) {
            try {
                // Gets the next operation
                Operation next = masterQueue.getNext();

                // If there was an Operation
                if (next != null) {
                    System.out.println(next);

                    // Obtain the node involved
                    String node = next.getParam().split(Operation.SEPARATOR)[0];

                    switch (next.getType()) {
                        case DELETE:
                            opDelete(node, next);
                            break;
                        case MKDIR:
                            opMkdir(node, next);
                            break;
                        case LISTDIR: // ALWAYS LOCAL
                            String path = next.getParam();
                            opListdir(path);
                            break;
                        case TRANSFER:
                            // Note: An operation transfer will never has the sender node as the local node.
                            // Transfer operation means that this node's user is requesting to send a file
                            // from a remote node, to a wether other remote node or this local node.
                            // RemoteReceiver will receive the message for a transfer, and charge to the master
                            // queue a Send operation, which means the file to send is in this local node.
                        case SEND:
                            // Pass the operation to the corresponding RemoteSender
                            remoteSenderThreads.get(node).addOperation(next);
                            break;
                    }
                    sleep(3000);
                }
                // If there's no more Operations, waits before checking again
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

    /*  LOCAL ONLY METHODS  */

    /**
     * Initializes the needed directories of the Network Cloud system. Checks if already exist,
     * and if not, creates them.
     */
    private void initSystemDir() {
        String OS = System.getProperty("os.name").toLowerCase();

        String[] dirlist = {
            "root",
            "backup",
            "sysfiles",
            "sysfiles/queues"
        };

        for (String dirname : dirlist) {
            // If OS is UNIX-like (MacOS, Linux)
            if (!dirname.equals("root") && (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))) dirname = "." + dirname;

            File dir = new File(systemDirectory + "/" + dirname);
            if (!dir.exists()) {
                dir.mkdir();
                // Every system dir except /root has to be hidden
                if (!dirname.equals(dirlist[0]))
                    try {
                        // If OS is Windows
                        if (OS.contains("win")) Files.setAttribute(dir.toPath(), "dos:hidden", true);
                    } catch (IOException e) {
                        e.printStackTrace(); // ??
                    }
            }
            // What about existing a file with the name of the dirs? (without ext)
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
     * <code>DELETE</code> {@link model.Operation Operation} process.
     * @param node the involved node.
     * @param next the {@link model.Operation Operation} to do.
     * @throws InterruptedException
     */
    private void opDelete(String node, Operation next) throws InterruptedException {
        // Blocks system until deleting finnishes
        requestDelete();

        // If local, Gets the path and tries to delete the file or directory
        if (node.equals(name)) {
            String path = next.getParam().split(Operation.SEPARATOR)[1];
            // Semaphore needed!
            if (delete(path)) {
                // Update GUIs
            }
            else {
                // Error message?
            }
        }
        // Else, pass this operation to the corresponding RemoteSender
        else {
            remoteSenderThreads.get(node).addOperation(next);
        }

        // Deleting finnished
        endDelete();
    }

    /**
     * <code>MKDIR</code> {@link model.Operation Operation} process.
     * @param node the involved node.
     * @param next the {@link model.Operation Operation} to do.
     */
    private void opMkdir(String node, Operation next) {
        // If local, Gets the path and tries to create the directory
        // Semaphore needed!
        if (node.equals(name)) {
            String path = next.getParam().split(Operation.SEPARATOR)[1];
            // Semaphore needed!
            if (createDirectory(path)) {
                // Update GUIs
            }
            else {
                // Error message?
            }
        }
        // Else, pass this operation to the corresponding RemoteSender
        else {
            remoteSenderThreads.get(node).addOperation(next);
        }
    }

    /**
     * <code>LISTDIR</code> {@link model.Operation Operation} process.
     * @param path the local path to display.
     */
    private void opListdir(String path) {
        // Semaphore needed!
        if (isCloudDir(path)) {
            ArrayList<String> filesInfo = new ArrayList<>();

            for (File file : new File(root.getAbsolutePath() + "/" + path).listFiles()) {
                filesInfo.add(file.getName() + Operation.SEPARATOR + file.lastModified() + Operation.SEPARATOR + file.isDirectory());
            }

            listdir(filesInfo);
        }
        else {
            // Couldn't find local dir
        }
    }

    /*  SYSTEM FUNCTIONS   */

    /**
     * Add a new {@link model.RemoteReceiver} thread to the list. This method has
     * to be called only by the {@link model.ConnectionPoint ConnectionPoint} thread,
     * wich must be previously created.
     * @param thread the new instance of {@link model.RemoteReceiver RemoteReceiver}.
     */
    public void addRemoteReceiver(RemoteReceiver thread) {
        thread.setMutex(recv);
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
    private boolean createDirectory(String path) {
        return new File(root.getAbsolutePath() + "/" + path).mkdir();
    }

    /**
     * Removes from the local OS file system the specified file or directory.
     * @param path the <code>systemDirectory root</code> relative path (file or directory 
     * name included) where is located the file/directory to delete.
     * @return <code>true</code> if and only if the file or directory is successfully 
     * deleted; <code>false</code> otherwise.
     */
    private boolean delete(String path) {
        return new File(root.getAbsolutePath() + "/" + path).delete();
    }

    /**
     * @return the name of this node.
     */
    public String getNodeName() {
        return name;
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

    /**
     * Checks if a directory (of this Network Cloud) exists.
     * @param path the path relative to <code>systemDirectory root</code>.
     * @return <code>true</code> if exists, <code>false</code> otherwise.
     */
    public boolean isCloudDir(String path) {
        File dir = new File(root.getAbsolutePath() + "/" + path);
        return dir.exists() && dir.isDirectory();
    }
    
    /**
     * <b>GUI Interface</b>
     * Sends to the {@link controller.Controller controller} the list of files to
     * display in the GUI.
     * @param files the list of files (filename, last modified time in millis and
     * if its dir or not) to display.
     */
    public void listdir(ArrayList<String> files) {
        controller.listFiles(files);;
    }

    /*  DELETE - SEND SYNCRHONIZATION METHODS   */

    /**
     * Request the system for a <code>SEND</code> {@link model.Operation Operation}.
     * System cannot <code>DELETE</code> if is sending data.
     * @throws InterruptedException
     */
    public void requestSend() throws InterruptedException {
        z.acquire();
            send.acquire();
                x.acquire();
                    if (++sendingCount == 1) del.acquire();
                x.release();
            send.release();
        z.release();
    }

    /**
     * Notifies that a <code>SEND</code> {@link model.Operation Operation} has ended.
     * @throws InterruptedException
     */
    public void endSend() throws InterruptedException {
        x.acquire();
            if (--sendingCount == 0) del.release();
        x.release();
    }

    /**
     * Request the system for a <code>DELETE</code> {@link mode.Operation Operation}.
     * System cannot <code>SEND</code> if is deleting data.
     * @throws InterruptedException
     */
    private void requestDelete() throws InterruptedException {
        y.acquire();
            send.acquire();
        y.release();
        del.acquire();
    }

    /**
     * Notifies that a <code>DELETE</code> {@link model.Operation Operation} has ended.
     * @throws InterruptedException
     */
    private void endDelete() throws InterruptedException {
        del.release();
        y.acquire();
            send.release();
        y.release();
    }

}