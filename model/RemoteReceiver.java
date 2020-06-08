package model;

import java.net.Socket;

public class RemoteReceiver extends Thread {
    private Socket socket;

    public RemoteReceiver(Socket socket){
        this.socket = socket;
    }
}