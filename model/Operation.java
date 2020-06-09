package model;

/**
 * Class that represents a "System Operation" (see documentation). The operations are
 * the instructions for interact with the user and remote nodes. A "local operation"
 * means that the operation must do something in the local file system. A "Remote
 * operation" is an operation that must be sended to a remote node to become "local"
 * on that node.
 * <p>
 * The type of an operation indicates what has to do. The types are:
 * <blockquote>
 * <b>Upload file: "upld"</b><p>
 * <b>Donwload file: "dwnld"</b><p>
 * <b>Transfer file: "transf"</b><p>
 * <b>Send file: "send"</b><p>
 * <b>Delete file or directory: "del"</b><p>
 * <b>Create directory: "mkdir"</b><p>
 * <b>List directory content: "ls"</b><p>
 * <b>Confirm operation: "conf"</b><p>
 * <b>Mark failed operation: "fail"</b><p>
 * </blockquote>
 * For more information see documentation/System Operations
 */

public class Operation {
    /**
     * Means that the status of the operation cannot be determinated. The corresponding
     * reply hasn't arrived.
     */
    public static final int STATUS_UNKNOWN = 0;

    /**
     * Means that the operation has concluded successfully.
     */
    public static final int STATUS_CONFIRMED = 1;

    /**
     * Means that the operation has failed in the process.
     */
    public static final int STATUS_FAILED = 2;

    /**
     * The unique identifier for an instance of this class. The id is created with
     * the name of the node and, if "Remote operation", with the name of the remote
     * node.
     * <p>
     * <b>In case of remote:</b> <i>this_node_name/remote_node_name/serial_number</i>
     * <p>
     * <b>In case of local:</b> <i>this_node_name/local/serial_number</i>
     */
    private String id;

    /**
     * The type of the operation.
     */
    private String type;

    /**
     * Additional message, if needed by the type of the operation. For example the
     * nodes involved. Also includes the id of the operation that must be confirmed,
     * in case of "Remote Operations"
     */
    private String msg;

    /**
     * The current status of the operation.
     */
    private int status;

    /**
     * Constructs an instance of Operation specifing all atributes separately
     * @param local this local node name.
     * @param remote remote node name. "local" in case of local operations.
     * @param counter operation serial number.
     * @param type the type of the operation.
     * @param msg the additional information.
     * @param status the current status.
     */
    public Operation(String local, String remote, int counter, String type, String msg, int status) {
        this.id = local + "/" + remote + "/" + counter;
        this.type = type;
        this.msg = msg;
        this.status = status;
    }

    /**
     * Constructs an instance of Operation specifing all atributes as a comma separated
     * values.
     * @param csv the string that represents this operation. This string is structured
     * as overrided <code>{@link #toString()}</code> method does.
     */
    public Operation(String csv) {
        String[] parts = csv.split(",");
        id = parts[0];
        type = parts[1];
        msg = parts[2];
        status = Integer.parseInt(parts[3]);
    }

    @Override
    public String toString() {
        return id +"," + type + "," + msg + "," + status;
    }

    /**
     * @return this Operation instance's <code>{@link #id}</code>.
     */
    public String getId() {
        return id;
    }

    /**
     * @return this Operation instance's <code>{@link #type}</code>.
     */
    public String getType() {
        return type;
    }

    /**
     * @return this Operation instance's <code>{@link #msg}</code>.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @return this Operation instance's <code>{@link #status}</code>.
     */
    public int getStatus() {
        return status;
    }
}