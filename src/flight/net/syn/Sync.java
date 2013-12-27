package flight.net.syn;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

@SuppressWarnings("serial")
public abstract class Sync implements Serializable {

	Sync() {}

	Sync(int id, byte[] data) {
		setId(id);
		setData(data);
	}

	private int				id			= 0;

	private boolean			updated		= false;
	protected SyncRegistry	registry	= null;

	protected ByteBuffer	data		= null;

	public int getId() {
		return id;
	}

	public byte getClientId() {
		return (byte) ((getId() >> (8 * 3)) & 0xFF);
	}

	public void setId(int id) {
		this.id = id;
	}

	boolean isUpdated() {
		return updated;
	}

	void setUpdated(boolean updated) {
		this.updated = updated;
		if (this.updated && registry != null)
			registry.updated(this);
	}

	void setRegistry(SyncRegistry registry) {
		this.registry = registry;
	}

	public byte[] getData() {
		if (isUpdated() || data == null)
			writeValueToData();
		return data.array();
	}

	void setData(byte[] data) {
		this.data = ByteBuffer.wrap(data);
		readDataToValue();
	}

	protected abstract void readDataToValue();

	protected abstract void writeValueToData();

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		if (registry != null) {
			out.writeBoolean(true);
			out.writeInt(getId());
		} else {
			out.writeBoolean(false);
			out.writeByte(getData().length);
			out.write(getData());
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		if (in.readBoolean()) {
			setId(in.readInt());
		} else {
			byte[] data = new byte[in.readByte()];
			in.read(data, 0, data.length);
			setData(data);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass())
			return id == ((Sync) obj).id;
		else
			return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " id=" + id;
	}

}
