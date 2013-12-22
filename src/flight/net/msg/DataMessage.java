package flight.net.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataMessage extends Message {

	DataMessage() {}

	public DataMessage(byte source, byte[] data) {
		super(source);
		if (data != null)
			this.data = data;
		else
			throw new IllegalArgumentException();
	}

	private byte[]	data;

	public byte[] getData() {
		return data;
	}

	@Override
	void read(ObjectInputStream stream) throws IOException,
			InstantiationException, IllegalAccessException {
		super.read(stream);
		data = new byte[stream.readInt()];
		stream.read(data, 0, data.length);
	}

	@Override
	void write(ObjectOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeInt(data.length);
		stream.write(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj)) {
			byte[] objData = ((DataMessage) obj).data;
			if (data.length == objData.length) {
				for (int i = 0; i < data.length; ++i)
					if (data[i] != objData[i])
						return false;
				return true;
			} else
				return false;
		} else
			return false;
	}

	@Override
	public String toString() {
		String string = super.toString() + " data={";
		for (int i = 0; i < data.length; ++i) {
			if (i > 0)
				string += " ";
			string += String.format("%02X", data[i]);
		}
		return string + "}";
	}

}
