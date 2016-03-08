package features.bursts;

import java.util.ArrayList;
import java.util.Collection;

import keystroke.KeyStroke;

public class BurstBuilder {
	
	private ArrayList<KeyStroke> events;
	private Burst burst;
	private int startIndex;
	private int seekIndex;
	private int nextIndex;
	private int endIndex;

	public BurstBuilder(Burst burst) {
		this.burst = burst;
	}
	
	public void setEventStream(Collection<KeyStroke> events) {
		this.events = new ArrayList<KeyStroke>(events);
		initialize();
	}
	
	private void initialize() {
		startIndex = seekIndex = 0;
		endIndex = 0;
		nextIndex=0;
		
		try {
			//find initial keypress
			while (!isStartAKeypress()) {
				startIndex++;
			}
			//find initial delimiter
			while (!isStartIndexOnDelimeter())
				startIndex = seekIndex;
			nextKeyPress();
			endIndex = seekIndex;
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			//everything else should be ok...
		}
	}

	private KeyStroke nextKeyPress() throws NullPointerException {
		if (hasNextKeyPress()) {
			seekIndex = nextIndex;
			return events.get(seekIndex);
		}
		return null;
	}
	
	private boolean hasNextKeyPress() {
		nextIndex = seekIndex;
		while (nextIndex < events.size() - 1) {
			nextIndex++;
			if (events.get(nextIndex).isKeyPress())
				return true;
		}
		return false;
	}
	
	private boolean isStartAKeypress() throws IndexOutOfBoundsException {
		return getStart().isKeyPress();
	}
	
	private KeyStroke getStart() throws IndexOutOfBoundsException {
		return events.get(startIndex);
	}
	
	@SuppressWarnings("unused")
	private KeyStroke getSeek() throws IndexOutOfBoundsException {
		return events.get(seekIndex);
	}
	
	private KeyStroke getEnd() throws IndexOutOfBoundsException {
		return events.get(endIndex);
	}
	
	private boolean isStartIndexOnDelimeter() throws NullPointerException {
		return burst.startDelimeter().isDelimiter(getStart(), nextKeyPress());
			
	}
	
	private boolean isEndIndexOnDelimeter() throws NullPointerException {

		return burst.endDelimeter().isDelimiter(getEnd(), nextKeyPress());
	}
	
	public boolean hasNextBurst() {
		try {
			while (!isEndIndexOnDelimeter()) {
				endIndex = seekIndex;
			}
			return true;
		} catch (NullPointerException e){
			return false;
		}
	}
	
	public Burst nextBurst() {
		ArrayList<KeyStroke> output = new ArrayList<KeyStroke>();
		for (int i = startIndex; i <= seekIndex;i++)
			output.add(events.get(i));
		startIndex = endIndex;
		endIndex = seekIndex;
		return burst.create(output);
	}
}
