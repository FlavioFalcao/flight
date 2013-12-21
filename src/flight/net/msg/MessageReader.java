package flight.net.msg;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class MessageReader {

	public MessageReader(InputStream stream) throws IOException {
		this(new ObjectInputStream(stream));
	}

	public MessageReader(ObjectInputStream stream) {
		this.stream = stream;
	}

	private ObjectInputStream	stream;

	public void close() throws IOException {
		stream.close();
	}

	public Message read() throws IOException, InstantiationException,
			IllegalAccessException {
		byte messageCode = stream.readByte();
		Class<? extends Message> messageClass = Message.messageCodes
				.get(messageCode);
		Message message = messageClass.newInstance();
		message.read(stream);
		return message;
	}

}
