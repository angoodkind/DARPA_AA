package keystroke;

import events.EventIterator;
import events.EventList;

public class KeyStrokeIterator extends EventIterator<KeyStroke> {

	public KeyStrokeIterator(EventList<KeyStroke> events) {
		super(events);
	}
	
	public boolean hasNextKeyPress() {
		int tempIndex = super.index;
		while (tempIndex < events.size()) {
			if (events.get(tempIndex).getID() == KeyStroke.KEY_PRESSED)
				return true;
			tempIndex++;
		} 
		return false;
	}

	public KeyStroke nextKeyPress() {
		if (hasNextKeyPress()) {
			while (hasNext()) {
				if (peekNext().getID() == KeyStroke.KEY_PRESSED)
					return next();
				next();
			}
		}
		return null;
	}

	public boolean hasNextKeyRelease() {
		int tempIndex = super.index;
		while (tempIndex < events.size()) {
			if (events.get(tempIndex).getID() == KeyStroke.KEY_RELEASED)
				return true;
			tempIndex++;
		} 
		return false;
	}

	public KeyStroke nextKeyRelease() {
		if (hasNextKeyPress()) {
			while (hasNext()) {
				if (peekNext().getID() == KeyStroke.KEY_RELEASED)
					return next();
				next();
			}
		}
		return null;
	}
}
