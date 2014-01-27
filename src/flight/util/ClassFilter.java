package flight.util;

/**
 * A special filter functor implementation that dynamically selects only objects
 * which are instances of a specified type.
 * 
 * @author Colby Horn
 * 
 * @param <E>
 *            the type of objects that this class can filter
 */
public class ClassFilter<E> implements Filter<E> {

	/**
	 * Creates a new {@link ClassFilter} that selects only objects which are of
	 * the same class, or a subclass, of the specified object. An instance
	 * object is needed in order to retrieve the type to be filtered upon at
	 * runtime after type erasure occurs...
	 * 
	 * @param instance
	 *            an object whose type is to be used in filtering
	 */
	public ClassFilter(E instance) {
		this.instance = instance;
	}

	private E	instance;

	@Override
	public boolean select(E element) {
		return instance.getClass().isInstance(element);
	}

}
