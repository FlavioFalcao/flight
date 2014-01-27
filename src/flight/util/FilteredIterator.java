package flight.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that only retrieves objects selected by its {@link Filter}.
 * 
 * @author Colby Horn
 * 
 * @param <E>
 *            the type of elements returned by this iterator
 */
public class FilteredIterator<E> extends ConstIterator<E> {

	/**
	 * Creates a new {@link FilteredIterator} set to return only those objects
	 * from the specified iterator that are selected by the given filter.
	 * 
	 * @param iterator
	 *            the iterator from which objects should be considered
	 * @param filter
	 *            the filter applied to the objects in the specified iterator;
	 *            only objects selected by this filter will be returned
	 */
	public FilteredIterator(Iterator<E> iterator, Filter<E> filter) {
		super(iterator);
		this.filter = filter;
		next = findNext();
	}

	private Filter<E>	filter;
	private E			next;

	private E findNext() {
		try {
			E next;
			while (!filter.select(next = super.next()));
			return next;
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	@Override
	public E next() {
		if (hasNext()) {
			E current = next;
			next = findNext();
			return current;
		} else
			throw new NoSuchElementException();
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

}
