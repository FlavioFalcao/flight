package flight.util;

public interface Filter<E> {

	public boolean select(E element);

}
