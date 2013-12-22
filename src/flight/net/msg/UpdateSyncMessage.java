package flight.net.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import flight.net.syn.Sync;

public class UpdateSyncMessage extends SyncMessage {

	UpdateSyncMessage() {}

	public UpdateSyncMessage(byte source, Sync sync) {
		this(source, sync.getId(), sync.getData());
	}
	
	public UpdateSyncMessage(byte source, int syncId, byte[] syncData) {
		super(source, syncId);
		this.syncData = syncData;
	}

	private byte[]	syncData;

	public byte[] getSyncData() {
		return syncData;
	}
	
	@Override
	void read(ObjectInputStream stream) throws IOException,
			InstantiationException, IllegalAccessException {
		super.read(stream);
		syncData = new byte[stream.readByte()];
		stream.read(syncData, 0, syncData.length);
	}

	@Override
	void write(ObjectOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeByte(syncData.length);
		stream.write(syncData);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj)) {
			byte[] objData = ((UpdateSyncMessage) obj).syncData;
			if (syncData.length == objData.length) {
				for (int i = 0; i < syncData.length; ++i)
					if (syncData[i] != objData[i])
						return false;
				return true;
			} else
				return false;
		} else
			return false;
	}

	@Override
	public String toString() {
		String string = super.toString() + " data={";
		for (int i = 0; i < syncData.length; ++i) {
			if (i > 0)
				string += " ";
			string += String.format("%02X", syncData[i]);
		}
		return string + "}";
	}


}
