package flight.net.syn;

import java.nio.ByteBuffer;

@SuppressWarnings("serial")
public class DoubleSync extends Sync {

	DoubleSync() {}

	public DoubleSync(double value) {
		value(value);
	}

	private double	value	= 0;

	public double value() {
		return value;
	}

	public void value(double value) {
		if (this.value != value) {
			this.value = value;
			setUpdated(true);
		}
	}

	@Override
	protected void readDataToValue() {
		value = data.getDouble();
	}

	@Override
	protected void writeValueToData() {
		data = ByteBuffer.allocate(Double.SIZE / 8).putDouble(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return value == ((DoubleSync) obj).value;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val=" + value;
	}

}
