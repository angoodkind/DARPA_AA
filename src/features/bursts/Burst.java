package features.bursts;

import java.util.ArrayList;

import keystroke.KeyStroke;

public abstract class Burst {

	private String name;
	ArrayList<KeyStroke> EventList;
	
	public Burst() {
	}
	
	public Burst(ArrayList<KeyStroke> keys){
		EventList = keys;
	}
	
	public KeyStroke getFirst() {
		return EventList.get(0);
	}
	
	public KeyStroke getLast() {
		return EventList.get(EventList.size()-1);
	}
	
	public KeyStroke get(int index) {
		return EventList.get(index);
	}
	
	public int length() {
		return EventList.size() - 1;
	}
	
	public int NumberOfEvents() {
		return EventList.size();
	}
	
	public long eventLength() {
		return this.getLast().getWhen() - this.getFirst().getWhen();
	}
	
	public abstract long burstTime();
	
	public abstract int burstWords();
	
	public abstract int burstChars();
	
	public abstract int burstAlphaChars();
	
	public abstract int burstWhiteSpaceChars();
	
	public abstract double burstAverageWordLength();
	
	public abstract BurstDelimiter startDelimeter();
	
	public abstract BurstDelimiter endDelimeter();
	
	public String barf() {
		return KeyStroke.keyStrokesToString(EventList);
	}
	
	public final String getBurstName() {
		return name;
	}
	
	public abstract Burst create(ArrayList<KeyStroke> keys);
}
