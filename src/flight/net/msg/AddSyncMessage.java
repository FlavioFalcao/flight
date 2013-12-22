package flight.net.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import flight.net.syn.Sync;
import flight.net.syn.SyncParser;

public class AddSyncMessage extends Message {

	AddSyncMessage() {}

	public AddSyncMessage(byte source, Sync sync) {
		super(source);
		this.sync = sync;
	}

	private Sync	sync;

	@Override
	void read(ObjectInputStream stream) throws IOException,
			InstantiationException, IllegalAccessException {
		super.read(stream);
		sync = SyncParser.readSync(stream);
	}

	@Override
	void write(ObjectOutputStream stream) throws IOException {
		super.write(stream);
		SyncParser.writeSync(stream, sync);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj)) {
			return sync.equals(((AddSyncMessage) obj).sync);
		} else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " sync={" + sync + "}";
	}

}
