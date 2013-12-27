package flight.net.syn;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import flight.net.err.SyncNotFoundException;
import flight.util.ConcatenatedIterator;
import flight.util.Filter;
import flight.util.FilteredIterator;

public class SyncRegistry implements Iterable<Sync> {

	public SyncRegistry() {}

	private Map<Integer, Sync>	syncs	= new ConcurrentHashMap<Integer, Sync>();

	public void register(Sync sync) {
		if (listener != null) {
			int id = listener.getHostId() << (8 * 3);
			synchronized (syncs) {
				while (syncs.containsKey(++id));
				syncs.put(id, sync);
			}
			sync.setId(id);
			if (listener != null)
				listener.syncRegistered(sync);
		}
		add(sync);
	}

	public void add(Sync sync) {
		if (sync != null) {
			sync.setRegistry(this);
			syncs.put(sync.getId(), sync);
		} else
			throw new NullPointerException();
	}

	public boolean contains(int syncId) {
		return syncs.containsKey(syncId);
	}

	public boolean contains(Sync sync) {
		if (sync != null) {
			return contains(sync.getId()) && sync.equals(get(sync.getId()));
		} else
			throw new NullPointerException();
	}

	public Sync get(int syncId) {
		return syncs.get(syncId);
	}

	public Sync remove(int syncId) {
		Sync sync = syncs.remove(syncId);
		if (sync != null)
			sync.setRegistry(null);
		if (listener != null)
			listener.syncRemoved(sync);
		return sync;
	}

	public Sync remove(Sync sync) {
		if (sync != null)
			return remove(sync.getId());
		else
			throw new NullPointerException();
	}

	public Sync update(int syncId, byte[] data) throws SyncNotFoundException {
		Sync sync = get(syncId);
		if (sync != null) {
			sync.setData(data);
			return sync;
		} else
			throw new SyncNotFoundException(syncId);
	}

	public Sync updateAndMark(int syncId, byte[] data)
			throws SyncNotFoundException {
		Sync sync = update(syncId, data);
		sync.setUpdated(true);
		return sync;
	}

	void updated(Sync sync) {
		if (listener != null)
			listener.syncUpdated(sync);
	}

	private SyncRegistryHost	listener	= null;

	public void addRegistryListener(SyncRegistryHost listener) {
		this.listener = listener;
	}

	@Override
	public Iterator<Sync> iterator() {

		// TODO: revise sync iteration order

		Filter<Sync> primitiveSyncFilter = new Filter<Sync>() {
			@Override
			public boolean select(Sync element) {
				return !(element instanceof ObjectSync);
			}
		};
		Filter<Sync> objectSyncFilter = new Filter<Sync>() {
			@Override
			public boolean select(Sync element) {
				return element instanceof ObjectSync;
			}
		};
		return new ConcatenatedIterator<Sync>(new FilteredIterator<Sync>(syncs
				.values().iterator(), primitiveSyncFilter),
				new FilteredIterator<Sync>(syncs.values().iterator(),
						objectSyncFilter));
	}

	public Iterator<Sync> iterator(Filter<Sync> filter) {
		return iteratorFiltered(filter);
	}

	private Iterator<Sync> iteratorFiltered(Filter<Sync> filter) {
		return new FilteredIterator<Sync>(iterator(), filter);
	}

	public Iterable<Sync> iterable(final Filter<Sync> filter) {
		return new Iterable<Sync>() {
			@Override
			public Iterator<Sync> iterator() {
				return iteratorFiltered(filter);
			}
		};
	}

	public Iterable<Sync> iterableUpdated() {
		return iterable(new Filter<Sync>() {
			@Override
			public boolean select(Sync element) {
				return element.isUpdated();
			}
		});
	}

}
