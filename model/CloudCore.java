package model;

import org.json.*;
import controller.Controller;

/*
    Class: CloudCore
    Description: The core of the system. It manages the Master Operation Queue, 
    delegate remote operations to the Subordinated Operation Queues and do all
    local operations.
    Also manages all files and File Tables.
*/

public class CloudCore extends Thread {
    private Controller controller;
    private JSONObject config;

    public CloudCore(Controller controller, JSONObject config) {
        this.controller = controller;
        this.config = config;
    }

    @Override
    public void run()  {
        System.out.println(config);
    }
}