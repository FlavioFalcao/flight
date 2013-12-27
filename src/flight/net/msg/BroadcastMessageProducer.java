package flight.net.msg;

import java.util.LinkedList;
import java.util.List;

public class BroadcastMessageProducer implements MessageProducer {

	private List<MessageHandler>	handlers	= new LinkedList<MessageHandler>();

	public void addMessageHandler(MessageHandler handler) {
		handlers.add(handler);
	}

	public MessageHandler[] getMessageHandlers() {
		return handlers.toArray(new MessageHandler[handlers.size()]);
	}

	public void broadcast(Message message) {
		for (MessageHandler handler : handlers)
			handler.handleMessage(message);
	}

	public void removeMessageHandler(MessageHandler handler) {
		handlers.remove(handler);
	}

}
