package flight.net.msg;

import static flight.global.Const.COULD_NOT_INSTANTIATE;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import flight.global.Logger;
import flight.util.BidirectionalMap;

public abstract class MessageParser {

	static BidirectionalMap<Byte, Class<? extends Message>>	messageCodes	= new BidirectionalMap<Byte, Class<? extends Message>>();

	static {
		messageCodes.put((byte) 0, NullMessage.class);
		messageCodes.put((byte) 1, EndTransmissionMessage.class);
		messageCodes.put((byte) 2, AcknowledgeMessage.class);
		messageCodes.put((byte) 3, AssignClientIDMessage.class);

		messageCodes.put((byte) 10, DataMessage.class);
		messageCodes.put((byte) 11, StringMessage.class);

		messageCodes.put((byte) 20, AddSyncMessage.class);
		messageCodes.put((byte) 21, UpdateSyncMessage.class);
		messageCodes.put((byte) 22, RemoveSyncMessage.class);
	}

	public static Message readMessage(ObjectInputStream stream)
			throws IOException {
		byte messageCode = stream.readByte();
		Class<? extends Message> messageClass = messageCodes.get(messageCode);
		try {
			Message message = messageClass.newInstance();
			message.read(stream);
			return message;
		} catch (InstantiationException | IllegalAccessException e) {
			Logger.logError(COULD_NOT_INSTANTIATE, messageClass);
			return null;
		}
	}

	public static void writeMessage(ObjectOutputStream stream, Message message)
			throws IOException {
		Class<? extends Message> messageClass = message.getClass();
		byte messageCode = messageCodes.inverse().get(messageClass);
		stream.writeByte(messageCode);
		message.write(stream);
	}

}
