package flight.global;

public abstract class Const {

	public static final String	COULD_NOT_INSTANTIATE		= "warning: could not instantiate class: %s";

	public static final String	CLIENT_CONNECTING			= "client connecting to server at %s:%d...";
	public static final String	CLIENT_STARTED				= "client connected with id = %d";
	public static final String	CLIENT_MESSAGE_SENT			= "client sent message: %s";
	public static final String	CLIENT_MESSAGE_RECEIVED		= "client received message: %s";
	public static final String	CLIENT_CONNECTION_LOST		= "client connection to server lost";
	public static final String	CLIENT_STOPPED				= "client stopped";

	public static final String	CLIENT_SYNC_NOT_FOUND		= "warning: client could not find sync with id = %d";

	public static final String	SERVER_STARTED				= "server started on port %d";
	public static final String	SERVER_CONNECTION_FAILED	= "warning: server failed to connect to new client";
	public static final String	SERVER_ADDED_CLIENT			= "server added new client (id = %d) at %s";
	public static final String	SERVER_REMOVED_CLIENT		= "server removed client (id = %d)";
	public static final String	SERVER_MESSAGE_RECEIVED		= "server received message: %s";
	public static final String	SERVER_CONNECTION_LOST		= "server connection lost to client (id = %d)";
	public static final String	SERVER_STOPPED				= "server stopped";

	public static final String	SERVER_SYNC_NOT_FOUND		= "warning: client could not find sync with id = %d";

	public static final String	OBJSYNC_SERIALIZE_ERROR		= "warning: sync object not serializable";
	public static final String	OBJSYNC_REFRESH_ERROR		= "warning: sync object could not be refreshed";

}
