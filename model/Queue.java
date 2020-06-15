package model;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * Class that represents the resource of an "Operation Queue".
 * <p>
 * An Operation Queue is a system file where all the {@link model.Operation Operations}
 * are appended and also obtained from. As a resource accessed by more than one
 * process, it's necessary guarantee that only one will be accessing it at time, so
 * it uses a <code>Semaphore</code> to this purpose.
 * It can represent both <b>Master</b> and <b>Subordinated</b> queues.
 * <p>
 * <b>Katsushika/2020/06/11:</b> <code>changeStatus()</code> method is no longer needed
 * due to the elimination of Operation replies.
 */

public class Queue {
    /**
     * The absolute path to where is located the queue this instance represents.
     */
    private String path;

    /**
     * A {@link model.FileHandler FileHandler} instance for reading and writing to
     * the queue file.
     */
    private FileHandler file;

    /**
     * The <code>Semaphore</code> to coordinate the process accessing the same queue.
     * Only one thread is alowed to access at time, so its constructed with only one
     * permit, and the waiting threads will access the queue in aquire order.
     */
    private Semaphore sem;

    /**
     * Creates a new {@link model.Operation Operation} queue.
     * @param path the location of the queue file.
     */
    public Queue(String path) {
        this.path = path;
        file = new FileHandler();
        sem = new Semaphore(1, true);
    }

    /**
     * Obtains the next {@link model.Operation Operation} in the secuence of the queue.
     * @return the {@link model.Operation Operation} at the start of the queue, or
     * <code>null</code> if the queue file doesn't exists, or there's no more Operations.
     * @throws InterruptedException - if the thread accessing the resource gets
     * interrumpted.
     * @throws IOException - if there's a problem with the queue file.
     */
    public Operation getNext() throws InterruptedException, IOException {
        // Block access
        sem.acquire();
            // If the queue doesn't exist or is empty
            if (!file.open(path, "r")) {
                sem.release();
                return null;
            };

            String aux = file.readline();
            if (aux == null || aux.isEmpty()) {
                sem.release();
                return null;
            }

            // Otherwise, the queue exists and has Operations
            Operation next = new Operation(aux);

            // The returned Operation is deleted from the queue
            String surplus = file.read();
            file.close();

            file.open(path, "w");
            file.write(surplus);
            file.close();
        sem.release();

        return next;
    }

    /**
     * Appends the specified {@link model.Operation Operation} at the end of the 
     * queue file. If the file doesn't exist yet, then it's first created, and 
     * then the operation added.
     * @param op the {@link model.Operation Operation} instance to be appended.
     * @throws InterruptedException - if the thread accessing the resource gets
     * interrumpted.
     * @throws IOException - if there's a problem with the queue file.
     */
    public void add(Operation op) throws InterruptedException, IOException {
        // Block access
        sem.acquire();
            // If the queue exists and has other Operations
            StringBuilder content = new StringBuilder();
            if (file.open(path, "r")) {
                content.append(file.read());
                file.close();
            }

            // Then, add new Operation to the end
            content.append(op.toString());
            file.open(path, "w");
            file.write(content.toString());
            file.close();
        sem.release();
    }
}