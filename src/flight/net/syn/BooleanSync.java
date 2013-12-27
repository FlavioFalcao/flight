package flight.net.syn;

import java.nio.ByteBuffer;

@SuppressWarnings("serial")
public class BooleanSync extends Sync {

	BooleanSync() {}

	public BooleanSync(boolean value) {
		value(value);
	}

	private boolean	value	= false;

	public boolean value() {
		return value;
	}

	public void value(boolean value) {
		if (this.value != value) {
			this.value = value;
			setUpdated(true);
		}
	}

	@Override
	protected void readDataToValue() {
		value = data.get() != 0;
	}

	@Override
	protected void writeValueToData() {
		data = ByteBuffer.allocate(Byte.SIZE / 8).put((byte) (value ? 1 : 0));
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return value == ((BooleanSync) obj).value;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val=" + value;
	}

}
