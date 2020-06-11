package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

import model.Operation.Status;
import model.Operation.Type;

/**
 * RemoteReceiver is a <code>Thread</code> for receive messages an data from remote
 * nodes. It's created when the {@link model.ConnectionPoint ConnectionPoint}
 * server receives an incomming connection.
 * If the remote node this thread is managing gets disconnected, then this thread
 * is interrumpted. A new one will be created when the remote node gets connected again.
 * When receives a message, this thread manages to put the corresponding operation in
 * the {@link model.Queue Master Queue}.
 */

public class RemoteReceiver extends Thread {
    // System core
    private CloudCore core;
    
    // Communication Socket
    private Socket receiver;

    /**
     * Creates an instance of this thread to manage incoming messages and data.
     * @param core this system {@link model.CloudCore core}.
     * @param receiver the socket created by the {@link model.ConnectionPoint ConnectionPoint}.
     */
    public RemoteReceiver(CloudCore core, Socket receiver){
        this.core = core;
        this.receiver = receiver;
        //receiver.setSoTimeout(timeout);
    }

    @Override
    public void run() {
        DataInputStream din = new DataInputStream(new BufferedInputStream(receiver.getInputStream()));

        BufferedOutputStream bf;
        byte[] b = new byte[4046];
        long file_size;
        int count;
 
        while (true) {
            switch(Operation.Type.valueOf(din.readUTF())){ //Reads operation
                case CONFIRM:
                    break;
                case FAIL:
                    break;
                case LISTDIR:
                    break;

                case DELETE:
                    core.addOperation(new Operation(core.getName(), "local", -1, Type.DELETE, din.readUTF(), Status.UNKNOWN));
                    break;
                case MKDIR:
                    core.addOperation(new Operation(core.getName(), "local", -1, Type.MKDIR, din.readUTF(), Status.UNKNOWN));
                    break;
                case TRANSFER:
                    core.addOperation(new Operation(core.getName(), "local", -1, Type.SEND, din.readUTF(), Status.UNKNOWN);
                    break;
                case SEND: //Receives a file and save it into the received files directory
                    bf = new BufferedOutputStream(new FileOutputStream(core.getReceivedFilesDirectory() + '/' + din.readUTF()));//Reads the file name
                    file_size = din.readLong(); //Reads the file size
                    //saves file by chunks
                    while (file_size > 0 && (count = din.read(b, 0, (int) Math.min(file_size, b.length))) != -1){
                        bf.write(b, 0, count);
                        bf.flush();
                        file_size -= count;
                    }
                    bf.close();
                    break;
                
            }
            
        }


    }
}