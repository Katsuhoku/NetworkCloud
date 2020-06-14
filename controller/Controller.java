package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONException;
import org.json.JSONObject;

import model.CloudCore;
import model.FileHandler;
import model.Operation;
import model.Operation.Type;
import views.MainWindow;

/**
 * Network System controller, an interface to communicate GUI and the system
 * {@link model.CloudCore core}. The controller starts the system core and the
 * GUI, then provide them all methdos to communicate.
 */

public class Controller {
    private CloudCore core;
    private MainWindow mainWindow;

    public static void main(String[] args) throws IOException {
        Controller controller = new Controller();
        controller.initCore();
        //controller.initWindow();
    }

    /* CONTROLLER ONLY */

    /**
     * Initializes the system {@link model.CloudCore core} thread, only if there's
     * no problem with the configuration file.
     */
    private void initCore() {
        FileHandler configFile = new FileHandler();

        // Check if config file is in directory
        if (configFile.open("config.json", "r")) {
            JSONObject config = new JSONObject();
            try {
                // Read file and convert it in JSON map
                config = new JSONObject(configFile.read());
                // Check if path for system files is actually a valid directory
                File systemDirectory = new File(config.getString("path"));
                if (systemDirectory.isDirectory()) {
                    core = new CloudCore(this, config);
                    core.start(); // Start core
                } else {
                    System.out.println("Fatal Error: Specified path in configuration file must be a directory.");
                    System.exit(-1);
                }
            } catch (JSONException | IOException e) {
                System.out.println("Fatal Error: Cannot read configuration file.");
                System.exit(-1);
            }

        } else {
            System.out.println("Fatal Error: Cannot open configuration file.");
            System.exit(-1);
        }
    }

    /**
     * Creates and initializes the window frame for the GUI.
     */
    private void initWindow(ArrayList<String> nodeNames) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        mainWindow = new MainWindow(this);
        mainWindow.start(nodeNames);
    }

    /*  COMMUNICATION WINDOW -> CORE    */

    /**
     * Depending on what node is the sender, constructs and passes to the
     * core the corresponding {@link model.Operation Operation}.
     * <p>
     * If the sender is this node, then constructs a <code>SEND</code> {@link
     * model.Operation Operation}.
     * If the sender is different from this node, then constructs a <code>TRANSFER</code>
     * {@link model.Operation Operation}, with the special parameter as "data".
     * In this case, the receiver can be this or another remote node.
     * @param sender the sender node.
     * @param receiver the receiver node.
     * @param path the {@link model.CloudCore core} <code>systemDirectory root</code>
     * relative path.
     */
    public void send(String sender, String receiver, String path) {
        // The sender is this node
        if (sender.equals(core.getNodeName())) {
            String param = receiver + Operation.SEPARATOR + path + Operation.SEPARATOR + Operation.SEND_DATA;
            core.addOperation(new Operation(Type.SEND, param));
        }
        else {
            String param = sender + Operation.SEPARATOR + receiver + Operation.SEPARATOR + path + Operation.SEPARATOR + Operation.SEND_DATA;
            core.addOperation(new Operation(Type.TRANSFER, param));
        }
    }

    /**
     * Requests the names and last modified time of the files and directories inside
     * the specified directory.
     * If local, it constructs a <code>LISTDIR</code> {@link model.Operation Operation},
     * which can be done instantly.
     * If remote, it constructs a <code>TRANSFER</code> {@link model.Operation Operation},
     * with the special parameter as "info", wich will request the remote node the 
     * content of the requested directory. This process can take some time.
     * @param node
     * @param path
     */
    public void listdir(String node, String path) {
        // The dir is local
        if (node.equals(core.getNodeName())) {
            core.addOperation(new Operation(Type.LISTDIR, path));
        }
        else {
            String param = node + Operation.SEPARATOR + core.getNodeName() + Operation.SEPARATOR + path + Operation.SEPARATOR + Operation.SEND_INFO;
            core.addOperation(new Operation(Type.TRANSFER, param));
        }
    }

    /**
     * Constructs a <code>MKDIR</code> {@link model.Operation Operation} and passes it
     * to the core.
     * @param node the node where is the path.
     * @param path the path (new directory name included) where will be created.
     */
    public void mkdir(String node, String path) {
        String param = node + Operation.SEPARATOR + path;
        core.addOperation(new Operation(Type.MKDIR, param));
    }

    /**
     * Constructs a <code>DELETE</code> {@link model.Operation Operation} and passes it
     * to the core.
     * @param node the node there is the path.
     * @param path the path (directory/file name included) where is the directory or
     * file to delete.
     */
    public void delete(String node, String path) {
        String param = node + Operation.SEPARATOR + path;
        core.addOperation(new Operation(Type.DELETE, param));
    }

    /**
     * Ends the system process by user request.
     */
    public void exit() {
        System.exit(0);
    }

    /*  COMMUNICATION CORE -> WINDOW    */

    /**
     * Sends to the GUI the file list to display.
     * @param files the file list information to display.
     */
    public void listFiles(String node, ArrayList<String> files) {
        mainWindow.getMainPanel().getFilesPanel(node).updateTableData(files);
    }

    /**
     * Notifies the GUI that the system is ready to use, and passes the list of
     * connected remote node names.
     * @param nodeNames the list of node names.
     */
    public void notifyReady(ArrayList<String> nodeNames) {
        // Connect to GUI
        // Before this method is called, the GUI must show only a charging screen,
        // with all buttons and options disabled.
        initWindow(nodeNames);
    }

    /**
     * Notifies the GUI that an internal error has ocurred. The GUI has to put
     * in the screen the passed message.
     * @param msg the message to display.
     */
    public void notifyError(String msg) {
        // Connect to GUI
    }


    /*                  E   V   E   N   T   O   S               */
    public void setBackButtonEnable(String nodeName, boolean b){
        mainWindow.getMainPanel().getFilesPanel(nodeName).setBackButtonEnable(b);
    }

    public void setSendButtonEnable(boolean b){
        mainWindow.getMainPanel().getOperationsPanel().setSendButtonEnable(b);
    }

    public void setOpenButtonEnable(boolean b){
        mainWindow.getMainPanel().getOperationsPanel().setOpenButtonEnable(b);
    }

    public void setDeleteButtonEnable(boolean b){
        mainWindow.getMainPanel().getOperationsPanel().setDeleteButtonEnable(b);
    }

    public void showFilesPanel(String nodeName){
        mainWindow.getMainPanel().showFilesPanel(nodeName);
        mainWindow.getMainPanel().getFilesPanel(nodeName).setOperationsButtons();
    }

    public void updatePath(String nodeName, String path){
        mainWindow.getMainPanel().getFilesPanel(nodeName).updatePath(path);
    }
}