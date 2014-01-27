package flight.net.syn;

import static flight.global.Const.OBJSYNC_REFRESH_ERROR;
import static flight.global.Const.OBJSYNC_SERIALIZE_ERROR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import flight.global.Logger;

@SuppressWarnings("serial")
public class ObjectSync<E> extends Sync {

	ObjectSync() {}

	public ObjectSync(E value) {
		if (value != null) {
			value(value);
		} else
			throw new NullPointerException();
	}

	private E	value	= null;

	public E value() {
		return value;
	}

	public void value(E value) {
		if (this.value == null || !this.value.equals(value)) {
			this.value = value;
			setUpdated(true);
		}
	}

	public void update() {
		setUpdated(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void readDataToValue() {
		ObjectInputStream reader;
		try {
			reader = new ObjectInputStream(new ByteArrayInputStream(
					data.array()));
			value = (E) reader.readObject();
			reader.close();
		} catch (IOException | ClassNotFoundException e) {
			value = null;
			Logger.logError(OBJSYNC_SERIALIZE_ERROR);
			e.printStackTrace();
		}
	}

	@Override
	protected void writeValueToData() {
		ObjectOutputStream writer;
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			writer = new ObjectOutputStream(buffer);
			writer.writeObject(value);
			data = ByteBuffer.wrap(buffer.toByteArray());
		} catch (IOException e) {
			data = null;
			Logger.logError(OBJSYNC_SERIALIZE_ERROR);
		}
	}

	@Override
	void setRegistry(SyncRegistry registry) {
		super.setRegistry(registry);
		if (registry != null)

			/*
			 * TODO revise complex object serialization and deserialization
			 */

			try {
				Field[] fields = value.getClass().getFields();
				for (Field field : fields) {
					if (field.get(value) instanceof Sync) {
						Sync sync = (Sync) field.get(value);
						if (sync != registry.get(sync.getId()))
							field.set(value, registry.get(sync.getId()));
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				Logger.logError(OBJSYNC_REFRESH_ERROR);
				e.printStackTrace();
			}
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() == obj.getClass() && super.equals(obj))
			return value.equals(((ObjectSync<?>) obj).value);
		else
			return false;
	}

	@Override
	public String toString() {
		return super.toString() + " val={" + value + "}";
	}

}
