package events;

import java.util.Collection;

public interface EventParser<E extends GenericEvent> {
	
	public Collection<E> parseSession(String encodedEventStream);

}
