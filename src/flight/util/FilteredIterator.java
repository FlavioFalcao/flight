package flight.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilteredIterator<E> extends ConstIterator<E> {

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
