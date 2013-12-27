package flight.net.syn;

import java.nio.ByteBuffer;

@SuppressWarnings("serial")
public class ByteSync extends Sync {

	ByteSync() {}

	public ByteSync(byte value) {
		value(value);
	}

	private byte	value	= 0;

	public byte value() {
		return value;
	}

	public void value(byte value) {
		if (this.value != value) {
			this.value = value;
			setUpdated(true);
		}
	}

	@Override
	protected void readDataToValue() {
		value = data.get();
	}

	@Override
	protected void writeValueToData() {
		data = ByteBuffer.allocate(Byte.SIZE / 8).put(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return value == ((ByteSync) obj).value;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val=" + value;
	}

}
