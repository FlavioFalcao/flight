package flight.net.msg;


public class AcknowledgeMessage extends Message {

	AcknowledgeMessage() {}

	public AcknowledgeMessage(byte source) {
		super(source);
	}

}
