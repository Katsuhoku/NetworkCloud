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
 * <b>Transfer file
 * <b>Send file
 * <b>Delete file or directory
 * <b>Create directory
 * <b>List directory content
 * <b>Confirm operation
 * <b>Mark failed operation
 * </blockquote>
 * For more information see documentation/System Operations
 */

public class Operation {
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
    private Type type;

    /**
     * Additional message, if needed by the type of the operation. For example the
     * nodes involved. Also includes the id of the operation that must be confirmed,
     * in case of "Remote Operations"
     */
    private String msg;

    public static final String SEPARATOR = "-";

    /**
     * The current status of the operation.
     */
    private Status status;

    /**
     * Constructs an instance of Operation specifing all atributes separately
     * @param local this local node name.
     * @param remote remote node name. "local" in case of local operations.
     * @param counter operation serial number.
     * @param type the type of the operation.
     * @param msg the additional information.
     * @param status the current status.
     */
    public Operation(String local, String remote, int counter, Type type, String msg, Status status) {
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
        type = Type.valueOf(parts[1]);
        msg = parts[2];
        status = Status.valueOf(parts[3]);
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
    public Type getType() {
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
    public Status getStatus() {
        return status;
    }

    /**
     * Operation Types.
     */
    public enum Type {
        TRANSFER,
        SEND,
        DELETE,
        MKDIR,
        LISTDIR,
        CONFIRM,
        FAIL
    }
    
    /**
     * Operation Statuses.
     */
    public enum Status {
        CONFIRMED,
        FAILED,
        UNKNOWN
    }
}