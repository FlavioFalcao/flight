package flight.net.msg;

import flight.net.syn.Sync;

public class RemoveSyncMessage extends SyncMessage {

	RemoveSyncMessage() {}

	public RemoveSyncMessage(byte source, Sync sync) {
		this(source, sync.getId());
	}

	public RemoveSyncMessage(byte source, int syncId) {
		super(source, syncId);
	}

}
