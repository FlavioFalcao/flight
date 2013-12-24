package flight.net.syn;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import flight.util.ConstIterator;

public class SyncRegistry implements Iterable<Sync> {

	public SyncRegistry(byte id) {
		this.id = id;
	}

	private byte				id;
	private Map<Integer, Sync>	syncs	= new ConcurrentHashMap<Integer, Sync>();

	public void addSync(Sync sync) {
		sync.setRegistry(this);
		syncs.put(sync.getId(), sync);
	}

	public void addNewSync(Sync sync) {
		int syncId = id << (8 * 3);
		synchronized (syncs) {
			while (contains(++syncId));
			sync.setId(syncId);
			addSync(sync);
		}
	}

	public boolean contains(int syncId) {
		return syncs.containsKey(syncId);
	}

	public boolean contains(Sync sync) {
		return contains(sync.getId()) && sync.equals(syncs.get(sync.getId()));
	}

	public Sync getSync(int syncId) {
		return syncs.get(syncId);
	}

	public void markSyncUpdated(Sync sync) {}

	public Sync removeSync(int syncId) {
		Sync sync = syncs.remove(syncId);
		if (sync != null)
			sync.setRegistry(null);
		return sync;
	}

	public Sync removeSync(Sync sync) {
		return removeSync(sync.getId());
	}

	public void updateSync(int syncId, byte[] data) {
		syncs.get(syncId).setData(data);
	}

	@Override
	public Iterator<Sync> iterator() {
		return new ConstIterator<Sync>(syncs.values().iterator());
	}

	public Iterator<Sync> iteratorUpdated() {
		return new UpdatedIterator(iterator());
	}

	private static class UpdatedIterator extends ConstIterator<Sync> {

		public UpdatedIterator(Iterator<Sync> iterator) {
			super(iterator);
		}

		private Sync	next;

		@Override
		public Sync next() {
			if (hasNext()) {
				Sync current = next;
				next = findNext();
				return current;
			} else
				throw new NoSuchElementException();
		}

		private Sync findNext() {
			Sync next;
			try {
				while (!(next = super.next()).isUpdated());
				return next;
			} catch (NoSuchElementException e) {
				return null;
			}
		}

		@Override
		public boolean hasNext() {
			return next == null;
		}

	};

}
