package events;

import java.util.Iterator;

public class EventIterator<E extends GenericEvent> implements Iterator<E> {

	protected EventList<E> events;
	protected int index;
	
	public EventIterator(EventList<E> events) {
		this.events = events;
		this.index=0;
	}

	@Override
	public boolean hasNext() {
		if  (events.size() > index)
			return true;
		return false;
	}

	@Override
	public E next() {
		return (E)events.get(index++);
	}
	
	protected E peekNext() {
		return (E)events.get(index);
	}
	
	@Override
	public final void remove() {
		// We don't want this to do anything!!
	}



}
