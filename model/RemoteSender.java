package model;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Remote sender is a <code>Thread</code> for send messages and data to remote
 * nodes. It's created by the system {@link model.CloudCore core} at the start.
 * If the remote node this thread is managing gets disconnected, then it will
 * try to reconnect until it gets connected again. It receives
 * {@link model.Operation Operations} from the core (in its {@link model.Queue
 * Subordinated Queue}), and manages the corresponding message.
 */

public class RemoteSender extends Thread {
    /**
     * This system {@link model.CloudCore core}.
     */
    private CloudCore core;

    /**
     * The name of the remote node. A string described in the config file.
     */
    private String remoteNodeName;

    /**
     * The IP Address of the remote node. Described as string in the config file.
     */
    private String remoteAddress;

    /**
     * The port number associated to the remote node {@link model.ConnectionPoint
     * ConenctionPoint}. Described as integer in the config file.
     */
    private int remotePort;

    /**
     * Socket for sending messages and data.
     */
    private Socket sender;

    /**
     * Subordinated {@link model.Queue Queue} this thread is managing. The
     * {@link model.Operation Operations} in this queue must be completed by the
     * remote node associated to this thread. Each thread of this class created by
     * the system {@link model.CloudCore core} will manage a Subordinated Queue.
     */
    private Queue subQueue;

    /**
     * Creates an instance of this thread to manage outcoming messages and data.
     * 
     * @param core       this system {@link model.CloudCore core}.
     * @param remoteNode a <code>JSONObject</code> that contains the remote node
     *                   information with the following structure:
     *                   <p>
     *                   <blockquote>
     * 
     *                   <pre>
     * {
     * "name": //remote node name,
     * "address": //remote node IP Address,
     * "port": //remote node ConnectionPort port
     * }
     *                   </pre>
     * 
     *                   </blockquote>
     * @see {@link model.ConnectionPoint ConnectionPoint}
     * @see {@link model.RemoteReceiver RemoteReceiver}
     */
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

        byte[] b = new byte[4096];
        int count;
        File f;
        BufferedInputStream bf;
        Operation op;
        DataOutputStream dout;

        while (true) {
            try {
                sender = new Socket(remoteAddress, remotePort);
                sender.shutdownInput();// This socket will be specifically used to send data
                dout = new DataOutputStream(new BufferedOutputStream(sender.getOutputStream()));
                dout.writeUTF(core.getNodeName());
                dout.flush();

                core.nodeStatus(remoteNodeName, true);
                while (!sender.isClosed()) {
                    if ((op = getNextOperation()) != null) {
                        //Sends the operation type name
                        dout.writeUTF(op.getType().name());
                        dout.flush();
                        switch (op.getType()){
                            case LISTDIR:
                                break;

                            case SEND:
                                // Blocks system until sending finnishes
                                core.requestSend();

                                String requested = op.getParam().split(Operation.SEPARATOR)[2];
                                String path = core.getSystemRootDirectory() + "/" + op.getParam().split(Operation.SEPARATOR)[1];
                                f = new File(path);

                                // Sends what was requested
                                dout.writeUTF(requested);
                                
                                // If the requested data is the file data
                                if (requested.equals(Operation.SEND_DATA)) {
                                    if (f.exists() && f.isFile()){
                                        bf = new BufferedInputStream(new FileInputStream(f));
                                        //Sends filename
                                        dout.writeUTF(f.getName());
                                        dout.flush();
                                        //Sends file size
                                        dout.writeLong(f.length());
                                        dout.flush();
                                        //Sends file data by chunks
                                        while ((count = bf.read(b)) != -1) {
                                            dout.write(b, 0, count);
                                            dout.flush();
                                        }
                                        //Sends file last modified
                                        dout.writeLong(f.lastModified());
                                        dout.flush();
                                        bf.close();

                                        core.putMessage("Sended: \"" + f.getName() + "\"");
                                    }
                                    else {
                                        // The file doesn't exists, or is directory, but
                                        // what to do?
                                    }
                                }
                                // Else, the requested data is directory info
                                else {
                                    if (f.exists() && f.isDirectory()) {
                                        ArrayList<String> filesInfo = new ArrayList<>();

                                        for (File file : f.listFiles()){
                                            // Structure: Filename/Last Modified Time (in millis)/true if directory, false otherwise
                                            filesInfo.add(file.getName() + Operation.SEPARATOR + file.lastModified() + Operation.SEPARATOR + file.isDirectory());
                                        }

                                        // Sends one by one
                                        dout.writeInt(filesInfo.size());
                                        dout.flush();
                                        for (int i = 0; i < filesInfo.size(); i++) {
                                            dout.writeUTF(filesInfo.get(i));
                                            dout.flush();
                                        }
                                    }
                                    else {
                                        System.out.println("Error?");
                                    }
                                }

                                // Sending has finnished
                                core.endSend();
                                
                                break;
                            case DELETE:
                            case MKDIR:
                            case TRANSFER: 
                                dout.writeUTF(op.getParam());
                                dout.flush();
                                break;
                        }
                    }
                }
            } catch (UnknownHostException e) {
                break; //El archivo de configuracion esta mal
            } catch (IOException e) {
                try{
                    if (sender != null && sender.isConnected())
                        sender.close();
                    Thread.sleep(500);
                }catch (InterruptedException | IOException e1) {
                    //Error?
                }
            } catch (InterruptedException e) {
                break; // ??
            }
        }
    }

    /**
     * Configurates this RemoteSender Thread's {@link model.Queue Subordinated
     * Queue}.
     */
    private void initQueue() {
        String queueFileName = remoteNodeName + ".q";
        subQueue = new Queue(core.getSystemQueuesDirectory() + "/" + queueFileName);
    }

    // Receive an Operation and append it to this RemoteSender's Subordinated Queue
    /**
     * Appends the received {@link model.Operation Operation} to this RemoteSender
     * Thread's {@link model.Queue Subordinated Queue}.
     * <p>
     * <b>Note:</b> This method, at first, will be called only by the system
     * {@link model.CloudCore core}.
     * 
     * @param op the operation to append
     */
    public void addOperation(Operation op) {
        try {
            subQueue.add(op);
        } catch (IOException ie) {
            System.out.println("Fatal Error: Cannot access to" + remoteNodeName + " Subordinated Queue");
        } catch (InterruptedException ioe) {
            System.out.println("Error: Current thread has stopped while accessing " + remoteNodeName + " Subordinated Queue.");
            System.out.println("More details:\n");
            ioe.printStackTrace();
        }
    }

    /**
     * Gets the next operation in the queue this thread is managing.
     * @return the next {@link model.Operation Operation} in the secuence of this 
     * {@link model.Queue Subordinated Queue}.
     */
    private Operation getNextOperation() {
        try {
            return subQueue.getNext();
        } catch (InterruptedException ie) {
            System.out.println("Fatal Error: Cannot access to" + remoteNodeName + " Subordinated Queue");
        } catch (IOException ioe) {
            System.out.println("Error: Current thread has stopped while accessing " + remoteNodeName + " Subordinated Queue.");
            System.out.println("More details:\n");
            ioe.printStackTrace();
        }
        return null;
    }

    /**
     * Closes this thread's socket to try reconnection. This method
     * will be called when de {@link model.RemoteReceiver RemoteReceiver}
     * thread detects that the remote node has gone disconnected.
     * @throws IOException
     */
    public void reconnect() throws IOException {
        sender.close();
    }
}