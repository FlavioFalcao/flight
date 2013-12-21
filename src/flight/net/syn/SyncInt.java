package flight.net.syn;

import java.nio.ByteBuffer;

public class SyncInt extends Sync {

	public SyncInt() {}

	public SyncInt(int value) {
		this.value = value;
	}

	public SyncInt(int id, byte[] data) {
		super(id, data);
	}

	private int	value	= 0;

	public int value() {
		return value;
	}

	public void updateValue(int value) {
		this.value = value;
		setUpdated(true);
	}

	@Override
	protected void readDataToValue() {
		value = data.getInt();
	}

	@Override
	protected void writeValueToData() {
		data = ByteBuffer.allocate(4).putInt(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return value == ((SyncInt) obj).value;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val=" + value;
	}

}
