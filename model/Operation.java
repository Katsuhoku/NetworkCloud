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
 * <b>Transfer file,
 * <b>Send file,
 * <b>Delete file or directory,
 * <b>Create directory,
 * <b>List directory content
 * </blockquote>
 * For more information see documentation/System Operations
 * <p>
 * <b>Katsushika/2020/06/11:</b> <code>id</code>, <code>status</code> and <code>
 * CONFIRM</code> and <code>FAIL</code> Operation Types, as <code>Status</code> enum
 * are no longer needed due to the elimination of Operation replies.
 * The <code>msg</code> field has been renamed to <code>param</code>.
 * 
 */

public class Operation {
    /**
     * The type of the operation.
     */
    private Type type;

    /**
     * Additional message, if needed by the type of the operation. For example the
     * nodes involved. In other words, the Operation parameters.
     */
    private String param;

    public static final String SEPARATOR = ":";

    public static final String SEND_DATA = "data";
    public static final String SEND_INFO = "info";

    /**
     * Constructs an instance of Operation specifing all atributes separately
     * @param local this local node name.
     * @param remote remote node name. "local" in case of local operations.
     * @param counter operation serial number.
     * @param type the type of the operation.
     * @param param the additional information.
     * @param status the current status.
     */
    public Operation(Type type, String param) {
        this.type = type;
        this.param = param;
    }

    /**
     * Constructs an instance of Operation specifing all atributes as a comma separated
     * values.
     * @param csv the string that represents this operation. This string is structured
     * as overrided <code>{@link #toString()}</code> method does.
     */
    public Operation(String csv) {
        String[] parts = csv.split(",");
        type = Type.valueOf(parts[0]);
        param = parts[1];
    }

    @Override
    public String toString() {
        return type + "," + param;
    }

    /**
     * @return this Operation instance's <code>{@link #type}</code>.
     */
    public Type getType() {
        return type;
    }

    /**
     * @return this Operation instance's <code>{@link #param}</code>.
     */
    public String getParam() {
        return param;
    }

    /**
     * Operation Types.
     */
    public enum Type {
        TRANSFER,
        SEND,
        DELETE,
        MKDIR,
        LISTDIR
    }
}