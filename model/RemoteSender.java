package model;

import java.net.Socket;
import org.json.JSONObject;
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
    private Socket receiver;

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

        // Try Ciclo infinito: Checar la conexión
        // Ciclo infinito: Checar la cola subordinada y envíar mensajes
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
}