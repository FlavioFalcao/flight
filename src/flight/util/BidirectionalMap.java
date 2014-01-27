package flight.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implements a bidirectional map that supports constant-time access of either
 * values by key or keys by value. The {@link #inverse()} method provides
 * inverse view access.
 * 
 * @author Colby Horn
 * 
 * @param <K>
 *            the initial view's key type
 * @param <V>
 *            the initial view's value type
 */
public class BidirectionalMap<K, V> implements Map<K, V> {

	/**
	 * Constructs a new, empty {@link BidirectionalMap}.
	 */
	public BidirectionalMap() {
		inverse = new BidirectionalMap<V, K>(this);
	}

	private BidirectionalMap(BidirectionalMap<V, K> inverse) {
		this.inverse = inverse;
	}

	/**
	 * Constructs a new {@link BidirectionalMap} containing all of the relations
	 * in the provided map.
	 * 
	 * @param m
	 *            a map whose relations are to be stored in this
	 *            {@link BidirectionalMap}
	 */
	public BidirectionalMap(Map<? extends K, ? extends V> m) {
		this();
		putAll(m);
	}

	private Map<K, V>				map	= new LinkedHashMap<K, V>();

	private BidirectionalMap<V, K>	inverse;

	/**
	 * Provides the inverse view of this {@link BidirectionalMap}. The inverse
	 * view is simply another {@link BidirectionalMap} instance with key and
	 * value type parameters reversed and filled with all of the inverted
	 * relations of this map.
	 * 
	 * @return the inverse view of this {@link BidirectionalMap}
	 */
	public BidirectionalMap<V, K> inverse() {
		return inverse;
	}

	@Override
	public void clear() {
		map.clear();
		inverse.map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public V put(K key, V value) {
		V oldValue = map.put(key, value);
		inverse.map.put(value, key);
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (java.util.Map.Entry<? extends K, ? extends V> entry : map
				.entrySet())
			put(entry.getKey(), entry.getValue());
	}

	@Override
	public V remove(Object key) {
		V value = map.remove(key);
		inverse.map.remove(value);
		return value;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

}
