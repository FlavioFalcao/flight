package flight.net.syn;

import java.nio.ByteBuffer;

@SuppressWarnings("serial")
public class ShortSync extends Sync {

	ShortSync() {}

	public ShortSync(short value) {
		value(value);
	}

	private short	value	= 0;

	public short value() {
		return value;
	}

	public void value(short value) {
		if (this.value != value) {
			this.value = value;
			setUpdated(true);
		}
	}

	@Override
	protected void readDataToValue() {
		value = data.getShort();
	}

	@Override
	protected void writeValueToData() {
		data = ByteBuffer.allocate(Short.SIZE / 8).putShort(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return value == ((ShortSync) obj).value;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val=" + value;
	}

}
