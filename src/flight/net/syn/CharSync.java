package flight.net.syn;

import java.nio.ByteBuffer;

@SuppressWarnings("serial")
public class CharSync extends Sync {

	CharSync() {}

	public CharSync(char value) {
		value(value);
	}

	private char	value	= 0;

	public char value() {
		return value;
	}

	public void value(char value) {
		if (this.value != value) {
			this.value = value;
			setUpdated(true);
		}
	}

	@Override
	protected void readDataToValue() {
		value = data.getChar();
	}

	@Override
	protected void writeValueToData() {
		data = ByteBuffer.allocate(Character.SIZE / 8).putChar(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return value == ((CharSync) obj).value;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val=" + value;
	}

}
