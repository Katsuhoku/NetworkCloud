package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import model.CloudCore;
import model.FileHandler;
import model.Operation;
import model.Operation.Type;
import views.FilesPanel;
import views.MainPanel;
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
        listdir(nodeNames.get(0), ".");
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
        mainWindow.getMainPanel().getOperationsPanel().setLoaderVisible(true);
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
        mainWindow.getMainPanel().getOperationsPanel().setLoaderVisible(true);
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
        mainWindow.getMainPanel().getOperationsPanel().setLoaderVisible(false);
    }

    /**
     * Notifies the GUI that the system is ready to use, and passes the list of
     * connected remote node names.
     * @param nodeNames the list of node names.
     */
    public void notifyReady(ArrayList<String> nodeNames) {
        initWindow(nodeNames);
    }

    /**
     * Notifies the GUI that an internal error has ocurred. The GUI has to put
     * in the screen the passed message.
     * @param msg the message to display.
     */
    public void notifyError(String msg) {
        mainWindow.getMainPanel().getFilesPanel(mainWindow.getMainPanel().getCurrentNode()).errorMessage(msg);
    }

    /**
     * Notifies the GUI a normal message.
     * @param msg the message to display
     */
    public void notifyMessage(String msg) {
        mainWindow.getMainPanel().getFilesPanel(mainWindow.getMainPanel().getCurrentNode()).putMessage(msg);
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
        MainPanel mp = mainWindow.getMainPanel();
        mp.showFilesPanel(nodeName);
        mp.getFilesPanel(nodeName).setOperationsButtons();
    }

    public void updatePath(String nodeName, String path){
        mainWindow.getMainPanel().getFilesPanel(nodeName).updatePath(path);
    }

    public void openEvent(){
        MainPanel mp = mainWindow.getMainPanel();
        String currentNode = mp.getCurrentNode();
        FilesPanel fp = mp.getFilesPanel(currentNode);

        String path = fp.getPath();
        if (path.isEmpty())
            path = path + "/" + fp.getSelectedFilename();
        else
            path = "/" + path + "/" + fp.getSelectedFilename();

        mainWindow.getMainPanel().getFilesPanel(mainWindow.getMainPanel().getCurrentNode()).errorMessage("");
        listdir(currentNode, "." + path);
        fp.updatePath(path);
        fp.setBackButtonEnable(true);
    }

    public void updateEvent(){
        MainPanel mp = mainWindow.getMainPanel();
        String currentNode = mp.getCurrentNode();
        mainWindow.getMainPanel().getFilesPanel(mainWindow.getMainPanel().getCurrentNode()).errorMessage("");
        listdir(currentNode, "./" + mp.getFilesPanel(currentNode).getPath());
    }

    public void backEvent(){
        MainPanel mp = mainWindow.getMainPanel();
        String currentNode = mp.getCurrentNode();
        FilesPanel fp = mp.getFilesPanel(currentNode);

        String path = fp.getPath();
        int last = path.lastIndexOf("/");

        mainWindow.getMainPanel().getFilesPanel(mainWindow.getMainPanel().getCurrentNode()).errorMessage("");

        if (last == -1){
            //Regresa a la carpeta raiz
            listdir(currentNode, ".");
            fp.updatePath("");
            fp.setBackButtonEnable(false);//Inhabilita el boton back
        }else{
            String newPath = path.substring(0, last);
            listdir(currentNode, "./" + newPath);
            fp.updatePath("/" + newPath);
        }
    }

    public void deleteEvent(){
        MainPanel mp = mainWindow.getMainPanel();
        String currentNode = mp.getCurrentNode();
        FilesPanel fp = mp.getFilesPanel(currentNode);
        String path = fp.getPath();
        if (path.isEmpty())
            path = path + "/" + fp.getSelectedFilename();
        else
            path = "/" + path + "/" + fp.getSelectedFilename();
        mainWindow.getMainPanel().getFilesPanel(mainWindow.getMainPanel().getCurrentNode()).errorMessage("");
        delete(currentNode, "." + path);
    }

    public void createDirectoryEvent(){
        mainWindow.getMainPanel().getFilesPanel(mainWindow.getMainPanel().getCurrentNode()).errorMessage("");
        String dn = JOptionPane.showInputDialog(mainWindow, "Directory name:", "Create directory", JOptionPane.INFORMATION_MESSAGE);
        if (dn != null){
            MainPanel mp = mainWindow.getMainPanel();
            String currentNode = mp.getCurrentNode();
            FilesPanel fp = mp.getFilesPanel(currentNode);
            String path = fp.getPath();
            if (path.isEmpty())
                path = path + "/" + dn;
            else
                path = "/" + path + "/" + dn;
            mkdir(currentNode, path);
        }
    }

    public void sendEvent(){
        mainWindow.getMainPanel().getFilesPanel(mainWindow.getMainPanel().getCurrentNode()).errorMessage("");
        Object[] nodes = new String[] {"B", "C", "D"}; //CAMBIAR NODOS
        String node = (String)JOptionPane.showInputDialog(mainWindow, "To:", "SendFile", JOptionPane.INFORMATION_MESSAGE, null, nodes, nodes[0]);
        if (node != null){
            MainPanel mp = mainWindow.getMainPanel();
            String currentNode = mp.getCurrentNode();
            FilesPanel fp = mp.getFilesPanel(currentNode);

            String path = fp.getPath();
            if (path.isEmpty())
                path = path + "/" + fp.getSelectedFilename();
            else
                path = "/" + path + "/" + fp.getSelectedFilename();
            send(currentNode, node, path);
        }
    }


}