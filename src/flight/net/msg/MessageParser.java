package flight.net.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import flight.util.BidirectionalMap;

public abstract class MessageParser {

	static BidirectionalMap<Byte, Class<? extends Message>>	messageCodes	= new BidirectionalMap<Byte, Class<? extends Message>>();

	static {
		messageCodes.put((byte) 0, NullMessage.class);
		messageCodes.put((byte) 1, StartTransmissionMessage.class);
		messageCodes.put((byte) 2, EndTransmissionMessage.class);
		messageCodes.put((byte) 3, AcknowledgeMessage.class);
		messageCodes.put((byte) 4, SetClientIDMessage.class);

		messageCodes.put((byte) 10, DataMessage.class);
		messageCodes.put((byte) 11, StringMessage.class);

		messageCodes.put((byte) 20, AddSyncMessage.class);
	}

	public static Message readMessage(ObjectInputStream stream)
			throws IOException, InstantiationException, IllegalAccessException {
		byte messageCode = stream.readByte();
		Class<? extends Message> messageClass = messageCodes.get(messageCode);
		Message message = messageClass.newInstance();
		message.read(stream);
		return message;
	}

	public static void writeMessage(ObjectOutputStream stream, Message message)
			throws IOException {
		Class<? extends Message> messageClass = message.getClass();
		byte messageCode = messageCodes.inverse().get(messageClass);
		stream.writeByte(messageCode);
		message.write(stream);
	}

}
