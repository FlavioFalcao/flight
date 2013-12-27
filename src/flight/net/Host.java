package flight.net;

import java.util.LinkedHashSet;
import java.util.Set;

import flight.net.msg.AcknowledgeMessage;
import flight.net.msg.AddSyncMessage;
import flight.net.msg.AssignClientIDMessage;
import flight.net.msg.BroadcastMessageProducer;
import flight.net.msg.EndTransmissionMessage;
import flight.net.msg.Message;
import flight.net.msg.MessageHandler;
import flight.net.msg.MessageProducer;
import flight.net.msg.NullMessage;
import flight.net.msg.RemoveSyncMessage;
import flight.net.msg.UpdateSyncMessage;
import flight.net.syn.SyncRegistry;
import flight.net.syn.SyncRegistryHost;

public abstract class Host implements MessageProducer {

	private byte	id	= -1;

	public byte getId() {
		return id;
	}

	protected void setId(byte id) {
		this.id = id;
	}

	protected void clearId() {
		setId((byte) -1);
	}

	protected Set<Class<? extends Message>>	caughtMessages;
	{
		caughtMessages = new LinkedHashSet<Class<? extends Message>>();
		caughtMessages.add(NullMessage.class);
		caughtMessages.add(EndTransmissionMessage.class);
		caughtMessages.add(AcknowledgeMessage.class);
		caughtMessages.add(AssignClientIDMessage.class);
		caughtMessages.add(AddSyncMessage.class);
		caughtMessages.add(UpdateSyncMessage.class);
		caughtMessages.add(RemoveSyncMessage.class);
	}
	
	protected SyncRegistry	registry = new SyncRegistry();
	protected SyncRegistryHost	registryListener;

	public SyncRegistry registry() {
		return registry;
	}

	protected BroadcastMessageProducer	messager	= new BroadcastMessageProducer();

	@Override
	public void addMessageHandler(MessageHandler handler) {
		messager.addMessageHandler(handler);
	}

	@Override
	public MessageHandler[] getMessageHandlers() {
		return messager.getMessageHandlers();
	}

	@Override
	public void removeMessageHandler(MessageHandler handler) {
		messager.removeMessageHandler(handler);
	}
	
}
