package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

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
    // System core
    private CloudCore core;

    // Communication Socket
    private Socket receiver;

    /**
     * Creates an instance of this thread to manage incoming messages and data.
     * 
     * @param core     this system {@link model.CloudCore core}.
     * @param receiver the socket created by the {@link model.ConnectionPoint
     *                 ConnectionPoint}.
     */
    public RemoteReceiver(CloudCore core, Socket receiver) {
        this.core = core;
        this.receiver = receiver;
        // receiver.setSoTimeout(timeout);
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
                        core.addOperation(new Operation(Type.SEND, din.readUTF()));
                        break;
                    case SEND: //Receives a file and save it into the received files directory
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
        } 
    }
}