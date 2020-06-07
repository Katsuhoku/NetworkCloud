package model;

import java.util.concurrent.Semaphore;

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