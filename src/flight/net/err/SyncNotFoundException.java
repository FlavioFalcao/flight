package flight.net.err;

@SuppressWarnings("serial")
public class SyncNotFoundException extends RuntimeException {

	public SyncNotFoundException(int syncId) {
		this.syncId = syncId;
	}

	public SyncNotFoundException(int syncId, String message) {
		super(message);
		this.syncId = syncId;
	}

	private int	syncId;

	public int getSyncId() {
		return syncId;
	}

}
