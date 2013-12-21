package flight.net.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataMessage extends Message {

	DataMessage() {}

	public DataMessage(byte source) {
		super(source);
	}

	public DataMessage(byte source, byte[] data) {
		super(source);
		this.data = data;
	}

	private byte[]	data	= null;
	
	public byte[] getData() {
		return data;
	}

	@Override
	void read(ObjectInputStream stream) throws IOException {
		super.read(stream);
		int length = stream.readInt();
		if (length < 0) {
			data = null;
		} else {
			data = new byte[length];
			stream.read(data, 0, data.length);
		}
	}

	@Override
	void write(ObjectOutputStream stream) throws IOException {
		super.write(stream);
		if (data == null) {
			stream.writeInt(-1);
		} else {
			stream.writeInt(data.length);
			for (byte b : data)
				stream.writeByte(b);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj)) {
			byte[] objData = ((DataMessage) obj).data;
			if (data == null && objData == null) {
				return true;
			} else if (data.length == objData.length) {
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
		String string = super.toString() + " data=";
		if (data == null) {
			string += "null";
		} else {
			string += "{";
			for (int i = 0; i < data.length; ++i) {
				if (i > 0)
					string += " ";
				string += String.format("%02X", data[i]);
			}
			string += "}";
		}
		return string;
	}

}
