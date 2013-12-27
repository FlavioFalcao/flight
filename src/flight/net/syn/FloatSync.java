package flight.net.syn;

import java.nio.ByteBuffer;

@SuppressWarnings("serial")
public class FloatSync extends Sync {

	FloatSync() {}

	public FloatSync(float x) {
		value(x);
	}

	private float	value	= 0;

	public float value() {
		return value;
	}

	public void value(float value) {
		if (this.value != value) {
			this.value = value;
			setUpdated(true);
		}
	}

	@Override
	protected void readDataToValue() {
		value = data.getFloat();
	}

	@Override
	protected void writeValueToData() {
		data = ByteBuffer.allocate(Float.SIZE / 8).putFloat(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return value == ((FloatSync) obj).value;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val=" + value;
	}

}
