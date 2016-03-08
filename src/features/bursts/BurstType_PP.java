package features.bursts;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extractors.data.Answer;
import keystroke.KeyStroke;

public class BurstType_PP extends Burst {
	
	//private final long b_time = 2000;
	//private PauseTimeDelimiter bd = new PauseTimeDelimiter(b_time);
	private int pause_bb = 0;
	private int pause_ab = 0;
	
	public BurstType_PP(ArrayList<KeyStroke> keys) {
		super(keys);
	}
	
	@Override
	public long burstTime() {
		long diff = getLastKeyPress().getWhen() - getFirstKeyPress().getWhen();
		return diff;
	}
	
	@Override
	public int burstChars() {
		//Get the first key code
		int char_count = 0;
		for (KeyStroke k : EventList) {
			if (k.isKeyPress() && k.isVisible()) 
				char_count++;
		}
		return char_count;
	}
	
	@Override
	public int burstAlphaChars() {
		//Get the first key code
		int alpha_count = 0;
		for (KeyStroke k : EventList) {
			if (k.isKeyPress() && k.isAlpha()) 
				alpha_count++;
		}
		return alpha_count;
	}
	
	public KeyStroke getFirstKeyPress() {
		//int i = 0;
		for (int i = 0; i < super.EventList.size(); i++) {
			KeyStroke k = super.EventList.get(i);
			if (k.isKeyPress()) {
				return super.EventList.get(i);
			}
		}
		return null;
	}
	
	public KeyStroke getLastKeyPress() {
		//System.out.println("Event list size-1: " + (super.EventList.size()-1));
		for (int i = super.EventList.size()-1; i >= 0; i--) {
			KeyStroke k = super.EventList.get(i);
			if (k.isKeyPress()) {
				return super.EventList.get(i);
			}
		}
		return null;
	}
	
	// Check if the current burst is useful for any purpose
	public Boolean isValid() {
		if (burstWords() > 0) 
			return true;
		else
			return false;
	}
	
	@Override
	public int burstWords() {
		//Get the first key code
		int word_count = 0;
		int char_count = 0;
		int cur_pos = 0;

		for (KeyStroke k : EventList) {

			if (k.isKeyPress() && k.isAlpha()) {
				char_count++;
				cur_pos = k.getCursorPosition();
			}

			if (k.isKeyRelease() && (k.isPunctuation() || k.isSpace())) {
				if (char_count > 0  && k.getCursorPosition() > cur_pos) {
					word_count++;
				}
				char_count = 0;
			}
		}
		// Last word
		if (char_count > 0) {
			word_count++;
			char_count = 0;
		}
		return word_count;
	}
	
	public int burstWordsFinal() {
		int word_count = 0;
		
		String final_text = KeyStroke.keyStrokesToFinalText(EventList);
		
		Pattern pattern = Pattern.compile("\\w+");
		Matcher matcher = pattern.matcher(final_text);
		while (matcher.find()) { word_count += 1; }
		
		return word_count;
	}
	
	public void SetPauseBefore(int val) { pause_bb = val; }
	public void SetPauseAfter(int val) { pause_ab = val; }
	
	public int GetPauseBefore() { return pause_bb; }
	public int GetPauseAfter() { return pause_ab; }
	
	//Undefined methods of the abstract class Burst	

	@Override
	public int burstWhiteSpaceChars() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double burstAverageWordLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BurstDelimiter startDelimeter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BurstDelimiter endDelimeter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Burst create(ArrayList<KeyStroke> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
