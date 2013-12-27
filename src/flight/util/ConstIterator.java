package flight.util;

import java.util.Iterator;

public class ConstIterator<E> implements Iterator<E> {

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
