package flight.net.syn;

import java.nio.ByteBuffer;

public abstract class Sync {

	Sync() {}

	Sync(int id, byte[] data) {
		setId(id);
		setData(data);
	}

	private int				id			= 0;
	
	private boolean			updated		= false;
	private SyncRegistry	registry	= null;

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
		if (isUpdated())
			writeValueToData();
		return data.array();
	}

	void setData(byte[] data) {
		this.data = ByteBuffer.wrap(data);
		readDataToValue();
	}

	protected abstract void readDataToValue();

	protected abstract void writeValueToData();

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
