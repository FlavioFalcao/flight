package flight.util;

import java.util.Iterator;

/**
 * An iterator that retrieves objects from two input iterators seamlessly in
 * sequence. Calls to {@link #next()} will initially return objects from the
 * first input iterator until it has no object remaining. Subsequent calls to
 * {@link #next()} will will then return objects from the second input iterator.
 * 
 * @author Colby Horn
 * 
 * @param <E>
 *            the type of elements returned by this iterator
 */
public class ConcatenatedIterator<E> extends ConstIterator<E> {

	/**
	 * Constructs a new {@link ConcatenatedIterator} around the two specified
	 * iterators.
	 * 
	 * @param iterator1
	 * @param iterator2
	 */
	public ConcatenatedIterator(Iterator<E> iterator1, Iterator<E> iterator2) {
		super(iterator1);
		this.iterator2 = iterator2;
	}

	Iterator<E>	iterator2;

	@Override
	public boolean hasNext() {
		return super.hasNext() || iterator2.hasNext();
	}

	@Override
	public E next() {
		return super.hasNext() ? super.next() : iterator2.next();
	}

}
