package events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import keystroke.KeyStroke;

public class EventList<E extends GenericEvent> implements Collection<E>, List<E> {

	private ArrayList<E> events;
	
	public EventList() {
		this.events = new ArrayList<E>();
	}
	
	public EventList(Collection<E> events) {
		this.events = new ArrayList<E>(events);
	}
	
	/**
	 * This method removes multiple, contiguous instances from an EventList
	 * @param startIndex	starting index of keystrokes to remove
	 * @param endIndex		ending index of keystrokes to remove
	 */
	public void removeAll(int startIndex, int endIndex) {
		int keyStrokesToRemove = endIndex - startIndex;
		  for (int i = 0; i < keyStrokesToRemove; i++)
			  this.remove(startIndex);
	}
	
	/**
	 * This returns the text of an EventList of KeyStrokes
	 * The !Character.isUnicodeIdentifierPart() test catches when the
	 * Ctrl key is down, which creates a non-visible, non-unicode character
	 * 9-12-15 - unicode issue seems to eliminate too many keystrokes - AG
	 * 
	 * If a backspace is encountered, the previous character added is deleted
	 * 
	 * @return The text of the typing session
	 */
	public String toVisibleTextString() {
		StringBuilder output = new StringBuilder();
		  for (E k : this) {
			  KeyStroke ks = (KeyStroke) k;
			  if (ks.isKeyPress()) {
				  if (ks.isBackspace() && output.length()>0) {
					  output.deleteCharAt(output.length()-1);
				  }
				  else if (ks.isVisible()) {
					  output.append(ks.getKeyChar());
				  }
			  }
		  }
		  return output.toString();
	}
	
	
	/**
	 * This returns an ArrayList of Longs, with the timestamp of each KeyStroke
	 * @return An ArrayList of timestamps
	 */
	public ArrayList<Long> toTimeArray() {
		  ArrayList<Long> timeArray = new ArrayList<Long>();
		  for (E k : this)
			  timeArray.add(k.getWhen());
		  return timeArray;
	  }
	

	
	@Override
	public boolean add(E k) {
		return events.add(k);
	}

	@Override
	public void clear() {
		events.clear();
	}

	@Override
	public boolean contains(Object o) {
		return events.contains(o);
	}

	@Override
	public boolean isEmpty() {
		return events.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return new EventIterator<E>(this);
	}

	@Override
	public boolean remove(Object o) {
		return events.remove(o);
	}

	@Override
	public int size() {
		return events.size();
	}

	@Override
	public Object[] toArray() {
		return events.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return events.toArray(a);
	}

	@Override
	public void add(int index, E k) {
		events.add(index, k);
	}

	@Override
	public E get(int index) {
		return events.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return events.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return events.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return events.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return events.listIterator(index);
	}

	@Override
	public E remove(int index) {
		return events.remove(index);
	}

	@Override
	public E set(int index, E k) {
		return events.set(index, k);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return events.subList(fromIndex, toIndex);
	}

	public E getFirst() {
		return events.get(0);
	}

	public E getLast() {
		return events.get(events.size() - 1);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
}
