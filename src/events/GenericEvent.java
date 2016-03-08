package events;


/**
 * It is assumed that, regardless of implementation,
 * every event will have a timestamp.
 * 
 * @author Patrick
 *
 */
public interface GenericEvent {
	
	public int getID();
	
	public long getWhen();
	
}
