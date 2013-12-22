package flight.net.msg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StringMessage extends Message {

	StringMessage() {}

	public StringMessage(byte source, String string) {
		super(source);
		if (string == null)
			throw new IllegalArgumentException();
		this.string = string;
	}

	private String	string;
	
	public String getString() {
		return string;
	}

	@Override
	void read(ObjectInputStream stream) throws IOException,
			InstantiationException, IllegalAccessException {
		super.read(stream);
		string = stream.readUTF();
	}

	@Override
	void write(ObjectOutputStream stream) throws IOException {
		super.write(stream);
		stream.writeUTF(string);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj)) {
			return string.equals(((StringMessage) obj).string);
		} else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " str=\"" + string + "\"";
	}

}
