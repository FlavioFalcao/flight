package flight.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import flight.util.BidirectionalMap;

public abstract class Message {

	static BidirectionalMap<Byte, Class<? extends Message>>	messageCodes	= new BidirectionalMap<Byte, Class<? extends Message>>();

	static {
		messageCodes.put((byte) 0, NullMessage.class);
		messageCodes.put((byte) 1, StartTransmissionMessage.class);
		messageCodes.put((byte) 2, EndTransmissionMessage.class);
		messageCodes.put((byte) 3, AcknowledgeMessage.class);
		messageCodes.put((byte) 4, SetClientIDMessage.class);

		messageCodes.put((byte) 10, DataMessage.class);
		messageCodes.put((byte) 11, StringMessage.class);
	}

	public Message() {}

	public Message(byte source) {
		setSource(source);
	}

	private byte	source	= -1;

	public byte getSource() {
		return source;
	}

	public void setSource(byte source) {
		this.source = source;
	}

	void read(ObjectInputStream stream) throws IOException {
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
