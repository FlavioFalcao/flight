package flight.global;

public abstract class Const {

	public static final String	CLIENT_CONNECTING		= "client connecting to server at %s:%d...";
	public static final String	CLIENT_STARTED			= "client started with id = %d";
	public static final String	CLIENT_HANDLING_MESSAGE	= "client received message: %s";
	public static final String	CLIENT_CONNECTION_LOST	= "client connection to server lost";
	public static final String	CLIENT_STOPPED			= "client stopped";

	public static final String	SERVER_STARTED			= "server started on port %d";
	public static final String	SERVER_ADDED_CLIENT		= "server added new client (id = %d) at %s";
	public static final String	SERVER_REMOVED_CLIENT	= "server removed client (id = %d)";
	public static final String	SERVER_MESSAGE_RECEIVED	= "server received message: %s";
	public static final String	SERVER_CONNECTION_LOST	= "server connection lost to client (id = %d)";
	public static final String	SERVER_STOPPED			= "server stopped";

}
