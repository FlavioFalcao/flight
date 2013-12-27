package flight.net.syn;

import java.nio.ByteBuffer;

public class IntSync extends Sync {

	IntSync() {}

	public IntSync(int value) {
		value(value);
	}

	private int	value	= 0;

	public int value() {
		return value;
	}

	public void value(int value) {
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
			return value == ((IntSync) obj).value;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val=" + value;
	}

}
