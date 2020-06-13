package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import model.Operation.Type;

/**
 * RemoteReceiver is a <code>Thread</code> for receive messages an data from
 * remote nodes. It's created when the {@link model.ConnectionPoint
 * ConnectionPoint} server receives an incomming connection. If the remote node
 * this thread is managing gets disconnected, then this thread is interrumpted.
 * A new one will be created when the remote node gets connected again. When
 * receives a message, this thread manages to put the corresponding operation in
 * the {@link model.Queue Master Queue}.
 * 
 * <p>
 * <b>Katsushika/2020/06/11:</b> Switch cases "CONFIRM" and "FAIL" are no longer
 * needed due to the elimination of {@link model.Operation Operation} replies.
 * Also, {@link model.Operation Operation} constructors only requires Operation {@link 
 * model.Operation.Type Type} and parameters.
 */

public class RemoteReceiver extends Thread {
    /**
     * This system {@link model.CloudCore core}.
     */
    private CloudCore core;

    /**
     * The name of the remote node this thread is receiving from.
     */
    private String remoteNodeName;

    /**
     * Socket for receiving data from other nodes.
     */
    private Socket receiver;

    /**
     * Semaphore for receiver's sychronization. Only one file or dir data can
     * be received at time.
     */
    private Semaphore mutex;

    /**
     * Creates an instance of this thread to manage incoming messages and data.
     * 
     * @param core     this system {@link model.CloudCore core}.
     * @param receiver the socket created by the {@link model.ConnectionPoint
     *                 ConnectionPoint}.
     */
    public RemoteReceiver(CloudCore core, Socket receiver, String remoteNodeName) {
        this.core = core;
        this.receiver = receiver;
        this.remoteNodeName = remoteNodeName;
    }

    public void setMutex(Semaphore mutex) {
        this.mutex = mutex;
    }

    @Override
    public void run() {
        DataInputStream din;
        File f;
        BufferedOutputStream bf;
        byte[] b = new byte[4046];
        long file_size;
        int count;


        try {
            din = new DataInputStream(new BufferedInputStream(receiver.getInputStream()));
            while (true) {
                switch(Operation.Type.valueOf(din.readUTF())){ //Reads operation
                    case LISTDIR:
                        break;
    
                    case DELETE:
                        core.addOperation(new Operation(Type.DELETE, din.readUTF()));
                        break;
                    case MKDIR:
                        core.addOperation(new Operation(Type.MKDIR, din.readUTF()));
                        break;
                    case TRANSFER:
                        String[] params = din.readUTF().split(Operation.SEPARATOR);
                        core.addOperation(new Operation(Type.SEND, params[1] + Operation.SEPARATOR + params[2] + Operation.SEPARATOR + params[3]));
                        break;
                    case SEND:
                        // Blocks the other receiving threads
                        mutex.acquire();

                        // This node's requested data
                        String receiving = din.readUTF();

                        // If is going to receive file data
                        if (receiving.equals(Operation.SEND_DATA)) {
                            //Receives a file and save it into the received files directory
                            f = new File(core.getReceivedFilesDirectory() + '/' + din.readUTF());//Reads the file name
                            bf = new BufferedOutputStream(new FileOutputStream(f));
                            //Reads file size
                            file_size = din.readLong(); 
                            //saves file by chunks
                            while (file_size > 0 && (count = din.read(b, 0, (int) Math.min(file_size, b.length))) != -1){
                                bf.write(b, 0, count);
                                bf.flush();
                                file_size -= count;
                            }
                            //Reads file last modified
                            f.setLastModified(din.readLong());
                            bf.close();
                        }
                        // Else, the incoming data is a remote directory content
                        else {
                            // Files list size
                            int size = din.readInt();

                            ArrayList<String> remoteFilesInfo = new ArrayList<>();
                            for (int i = 0; i < size; i++) {
                                remoteFilesInfo.add(din.readUTF());
                            }

                            // Then, passes the list to the core
                            core.listdir(remoteNodeName, remoteFilesInfo);
                        }
                        // Note: What if the requested dir/file wasn't available?
                        // Need add confirmation of existence

                        // File received
                        mutex.release();
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            // But, why?
        } catch (IOException e) {
            if (receiver.isConnected())
                try {
                    receiver.close();
                } catch (IOException e1) {
                    
                }
        } catch (InterruptedException e) {
            // ??
        }
    }

    public String getRemoteNodeName() {
        return remoteNodeName;
    }
}