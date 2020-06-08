package model;

import java.util.concurrent.Semaphore;

/*
    Class: Queue
    Description: Class that manages the resource of an "Operation Queue".
    An Operation Queue is a system file where all the operations are appended and
    also obtained from. As a resource accessed by more than one process, its necessary
    guarantee that only one will be accessing it at time, so it uses a Semaphore to this
    purpose.
    It can represent both Master and Subordinated queues.
*/

public class Queue {
    private String path;
    private FileHandler file;
    private Semaphore sem;

    public Queue(String path) {
        this.path = path;
        file = new FileHandler();
        sem = new Semaphore(1, true);
    }

    // Returns and removes the first Operation in the queue (file)
    public Operation getNext() {
        return null; // ****
    }

    // Appends the specified Operation at the end of de queue (file)
    public void add(Operation op) {

    }

    // Changes the state of the specified Operation
    public boolean changeState(String id, int state) {
        return true; // ****
    }
}