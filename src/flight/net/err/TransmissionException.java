package flight.net.err;

/**
 * An exception thrown when a network transmission does not follow expected
 * communication protocol.
 * 
 * @author Colby Horn
 */
@SuppressWarnings("serial")
public class TransmissionException extends Exception {

	/**
	 * Creates a new {@link TransmissionException}.
	 */
	public TransmissionException() {}

	/**
	 * Creates a new {@link TransmissionException} with the specified error
	 * message.
	 * 
	 * @param message
	 *            an verbose error message associated with this exception
	 */
	public TransmissionException(String message) {
		super(message);
	}

}
