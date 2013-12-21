package flight.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SetClientIDMessage extends Message {

	public SetClientIDMessage() {}

	public SetClientIDMessage(byte source) {
		super(source);
	}

	public SetClientIDMessage(byte source, byte newId) {
		super(source);
		this.newId = newId;
	}

	private byte	newId	= -1;
	
	public byte getNewId() {
		return newId;
	}

	@Override
	void read(ObjectInputStream stream) throws IOException {
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
			return newId == ((SetClientIDMessage) obj).newId;
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " newId=" + newId;
	}
}
