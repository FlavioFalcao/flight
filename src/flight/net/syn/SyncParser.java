package flight.net.syn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import flight.util.BidirectionalMap;

public abstract class SyncParser {

	public static BidirectionalMap<Byte, Class<? extends Sync>>	syncCodes	= new BidirectionalMap<Byte, Class<? extends Sync>>();

	static {
		syncCodes.put((byte) 0, IntSync.class);
	}

	public static Sync readSync(ObjectInputStream stream) throws IOException,
			InstantiationException, IllegalAccessException {
		byte syncCode = stream.readByte();
		Class<? extends Sync> syncClass = syncCodes.get(syncCode);
		Sync sync = syncClass.newInstance();
		byte[] syncData = new byte[stream.readByte()];
		stream.read(syncData);
		sync.setData(syncData);
		return sync;
	}

	public static void writeSync(ObjectOutputStream stream, Sync sync)
			throws IOException {
		Class<? extends Sync> syncClass = sync.getClass();
		byte syncCode = syncCodes.inverse().get(syncClass);
		stream.writeByte(syncCode);
		byte[] syncData = sync.getData();
		stream.writeByte(syncData.length);
		stream.write(syncData);
	}

}
