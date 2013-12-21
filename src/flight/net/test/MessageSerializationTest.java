package flight.net.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import flight.net.AcknowledgeMessage;
import flight.net.DataMessage;
import flight.net.EndTransmissionMessage;
import flight.net.Message;
import flight.net.MessageReader;
import flight.net.MessageWriter;
import flight.net.NullMessage;
import flight.net.SetClientIDMessage;
import flight.net.StartTransmissionMessage;
import flight.net.StringMessage;

public class MessageSerializationTest {

	public static void main(String[] args) throws IOException,
			InstantiationException, IllegalAccessException {

		byte id = 13, newId = 11;
		byte[] data1 = {}, data2 = { 4, 8, 15, 16, 23, 42 };
		List<Message> messages = new LinkedList<Message>();
		messages.add(new NullMessage(id));
		messages.add(new StartTransmissionMessage(id));
		messages.add(new EndTransmissionMessage(id));
		messages.add(new AcknowledgeMessage(id));
		messages.add(new SetClientIDMessage(id, newId));
		messages.add(new StringMessage(id, "Hello World!"));
		messages.add(new DataMessage(id, null));
		messages.add(new DataMessage(id, data1));
		messages.add(new DataMessage(id, data2));

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		MessageWriter writer = new MessageWriter(bytes);
		for (Message message : messages)
			writer.write(message);

		MessageReader reader = new MessageReader(new ByteArrayInputStream(
				bytes.toByteArray()));
		for (Message message : messages) {
			Message recovered = reader.read();
			if (recovered.equals(message)) {
				System.out.println("successful recovery: " + recovered);
			} else {
				System.out.println("failed recovery: " + message);
				System.out.println("  received instead: " + recovered);
			}
		}

	}
}
