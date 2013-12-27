package flight.util;

import java.util.Iterator;

public class ConcatenatedIterator<E> extends ConstIterator<E> {

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
