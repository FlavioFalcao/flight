package flight.net.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Message {

	Message() {}

	Message(byte source) {
		setSource(source);
	}

	private byte	source	= -1;

	public byte getSource() {
		return source;
	}

	public void setSource(byte source) {
		this.source = source;
	}

	void read(ObjectInputStream stream) throws IOException,
			InstantiationException, IllegalAccessException {
		source = stream.readByte();
	}

	void write(ObjectOutputStream stream) throws IOException {
		stream.writeByte(source);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass())
			return source == ((Message) obj).source;
		else
			return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " src=" + source;
	}
}
