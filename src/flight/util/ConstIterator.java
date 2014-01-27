package flight.util;

import java.util.Iterator;

/**
 * An iterator implementation that does not allow modification (via
 * {@link #remove()}) of the underlying data structure. Though the
 * {@link #remove()} method declaration is forced by {@link java.util.Iterator},
 * the {@link #remove()} method of this class (and its subclasses) will always
 * throw {@link UnsupportedOperationException}.
 * 
 * @author Colby Horn
 * 
 * @param <E>
 *            the type of elements returned by this iterator
 */
public class ConstIterator<E> implements Iterator<E> {

	/**
	 * Creates a new {@link ConstIterator} wrapping the specified iterator.
	 * 
	 * @param iterator
	 *            an iterator to be wrapped by this {@link ConstIterator}
	 */
	public ConstIterator(Iterator<E> iterator) {
		this.iterator = iterator;
	}

	private Iterator<E>	iterator;

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public E next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
