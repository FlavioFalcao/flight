package flight.util;

/**
 * A filter functor that provides a boolean {@link #select(Object)} method.
 * 
 * @author Colby Horn
 * 
 * @param <E>
 *            the type of objects that this class can filter
 */
public interface Filter<E> {

	/**
	 * Selects elements by return whether or not they should be removed by this
	 * filter.
	 * 
	 * @param element
	 *            an element to be considered by this filter
	 * @return true if the specified element should be kept, or selected, by
	 *         this filter; false otherwise
	 */
	public boolean select(E element);

}
