package flight.net.msg;

public interface MessageProducer {

	public void addMessageHandler(MessageHandler handler);

	public MessageHandler[] getMessageHandlers();

	public void removeMessageHandler(MessageHandler handler);

}
