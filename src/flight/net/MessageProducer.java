package flight.net;

import java.util.LinkedList;
import java.util.List;

public abstract class MessageProducer {

	private List<MessageHandler>	handlers	= new LinkedList<MessageHandler>();

	public void addMessageHandler(MessageHandler handler) {
		handlers.add(handler);
	}

	public MessageHandler[] getMessageHandlers() {
		return handlers.toArray(new MessageHandler[handlers.size()]);
	}

	public void broadcastMessage(Message message) {
		for (MessageHandler handler : handlers)
			handler.handleMessage(message);
	}

}
