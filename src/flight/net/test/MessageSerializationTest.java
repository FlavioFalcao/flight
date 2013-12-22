package flight.net.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import flight.net.msg.AcknowledgeMessage;
import flight.net.msg.AddSyncMessage;
import flight.net.msg.DataMessage;
import flight.net.msg.EndTransmissionMessage;
import flight.net.msg.Message;
import flight.net.msg.MessageReader;
import flight.net.msg.MessageWriter;
import flight.net.msg.NullMessage;
import flight.net.msg.RemoveSyncMessage;
import flight.net.msg.SetClientIDMessage;
import flight.net.msg.StartTransmissionMessage;
import flight.net.msg.StringMessage;
import flight.net.msg.UpdateSyncMessage;
import flight.net.syn.IntSync;
import flight.net.syn.Sync;

public class MessageSerializationTest {

	public static void main(String[] args) throws IOException,
			InstantiationException, IllegalAccessException {

		byte id = 13, newId = 11;
		byte[] data1 = {}, data2 = { 4, 8, 15, 16, 23, 42 };
		Sync sync = new IntSync(9);
		sync.setId(id);
		List<Message> messages = new LinkedList<Message>();
		messages.add(new NullMessage(id));
		messages.add(new StartTransmissionMessage(id));
		messages.add(new EndTransmissionMessage(id));
		messages.add(new AcknowledgeMessage(id));
		messages.add(new SetClientIDMessage(id, newId));
		messages.add(new StringMessage(id, "Hello World!"));
		messages.add(new DataMessage(id, data1));
		messages.add(new DataMessage(id, data2));
		messages.add(new AddSyncMessage(id, sync));
		messages.add(new UpdateSyncMessage(id, sync));
		messages.add(new RemoveSyncMessage(id, sync));

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
