package flight.net.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class SyncMessage extends Message {

	SyncMessage() {}

	SyncMessage(byte source, int syncId) {
		super(source);
		this.syncId = syncId;
	}

	private int	syncId;

	public int getSyncId() {
		return syncId;
	}

	@Override
	void read(ObjectInputStream stream) throws IOException,
			InstantiationException, IllegalAccessException {
		super.read(stream);
		syncId = stream.readInt();
	}

	@Override
	void write(ObjectOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeInt(syncId);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return syncId == ((SyncMessage) obj).syncId;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " id=" + syncId;
	}

}
