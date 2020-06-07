package model;

public class ConnectionPoint extends Thread {
    private int localPort;

    public ConnectionPoint(int localPort) {
        this.localPort = localPort;
    }

    @Override
    public void run() {
        
    }
}