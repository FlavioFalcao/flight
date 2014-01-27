package flight.net.err;

/**
 * An exception thrown when no synchronization field exists registered to the
 * specified id.
 * 
 * @author Colby Horn
 */
@SuppressWarnings("serial")
public class SyncNotFoundException extends RuntimeException {

	/**
	 * Creates a new {@link SyncNotFoundException}, recording the specified id.
	 * 
	 * @param syncId
	 *            the id that has no associated, registered synchronization
	 *            field
	 */
	public SyncNotFoundException(int syncId) {
		this.syncId = syncId;
	}

	/**
	 * Creates a new {@link SyncNotFoundException}, recording the specified id
	 * and error message.
	 * 
	 * @param syncId
	 *            the id that has no associated, registered synchronization
	 *            field
	 * @param message
	 *            the verbose error message associated with this exception
	 */
	public SyncNotFoundException(int syncId, String message) {
		super(message);
		this.syncId = syncId;
	}

	private int	syncId;

	/**
	 * Returns the id of the nonexistent synchronization field.
	 * 
	 * @return the id of the nonexistent synchronization field
	 */
	public int getSyncId() {
		return syncId;
	}

}
