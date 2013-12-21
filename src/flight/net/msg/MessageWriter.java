package flight.net.msg;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MessageWriter {

	public MessageWriter(OutputStream stream) throws IOException {
		this(new ObjectOutputStream(stream));
	}

	public MessageWriter(ObjectOutputStream stream) {
		this.stream = stream;
	}

	private ObjectOutputStream	stream;

	public void close() throws IOException {
		stream.close();
	}

	public void write(Message message) throws IOException {
		Class<? extends Message> messageClass = message.getClass();
		byte messageCode = Message.messageCodes.inverse().get(messageClass);
		stream.writeByte(messageCode);
		message.write(stream);
		stream.flush();
	}
}
