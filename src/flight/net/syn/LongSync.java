package flight.net.syn;

import java.nio.ByteBuffer;

@SuppressWarnings("serial")
public class LongSync extends Sync {

	LongSync() {}

	public LongSync(long value) {
		value(value);
	}

	private long	value	= 0;

	public long value() {
		return value;
	}

	public void value(long value) {
		if (this.value != value) {
			this.value = value;
			setUpdated(true);
		}
	}

	@Override
	protected void readDataToValue() {
		value = data.getLong();
	}

	@Override
	protected void writeValueToData() {
		data = ByteBuffer.allocate(Long.SIZE / 8).putLong(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return value == ((LongSync) obj).value;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val=" + value;
	}

}
