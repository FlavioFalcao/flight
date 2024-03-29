package flight.net.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AssignClientIDMessage extends Message {

	AssignClientIDMessage() {}

	public AssignClientIDMessage(byte source, byte newId) {
		super(source);
		this.newId = newId;
	}

	private byte	newId;
	
	public byte getNewId() {
		return newId;
	}

	@Override
	void read(ObjectInputStream stream) throws IOException,
			InstantiationException, IllegalAccessException {
		super.read(stream);
		newId = stream.readByte();
	}

	@Override
	void write(ObjectOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeByte(newId);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return newId == ((AssignClientIDMessage) obj).newId;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " newId=" + newId;
	}
}
