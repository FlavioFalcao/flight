package flight.net.syn;

import java.nio.ByteBuffer;

public abstract class Sync {

	Sync() {}
	
	Sync(int id, byte[] data) {
		setId(id);
		setData(data);
	}

	private int			id		= 0;
	private boolean		updated	= true;
	
	protected ByteBuffer	data	= null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	
	public byte[] getData() {
		if (isUpdated())
			writeValueToData();
		return data.array();
	}

	public void setData(byte[] data) {
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
