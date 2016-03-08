package features.bursts;

import java.util.ArrayList;

import keystroke.KeyStroke;

public class PPBurst extends Burst {
	
	BurstDelimiter bd = new PauseTimeDelimiter(2000);
	

	public PPBurst(ArrayList<KeyStroke> keys) {
		super(keys);
	}
	
	public PPBurst create(ArrayList<KeyStroke> keys) {
		return new PPBurst(keys);
	}
	
	public PPBurst() {
		super();
	}
	
	
	private int firstKeyPressIndex() {
		int i;
		int j = 0;
		for (i=0; i < EventList.size(); i++) {
			if (EventList.get(i).isKeyPress());
				j++;
			if (j > 1)
				return i;
		}
		
		return -1;
	}
	
	private int lastKeyPressIndex() {
		int i;
		int j = 0;
		for (i=EventList.size()-1; i > -1; i--) {
			if (EventList.get(i).isKeyPress());
				j++;
			if (j > 1)
				return i;
		}
		
		return -1;
	}
	
	public KeyStroke getFirstKeyPress() {
		return EventList.get(firstKeyPressIndex());
	}
	
	public KeyStroke getLastKeyPress() {
		return EventList.get(lastKeyPressIndex());
	}

	@Override
	public long burstTime() {
		return getLastKeyPress().getWhen()
				-getFirstKeyPress().getWhen();
	}

	@Override
	public int burstWords() {
		return 0;
	}

	public int burstKeyStrokes() {
		int index;
		int count = 0;
		for (index = firstKeyPressIndex(); 
				index < lastKeyPressIndex()+1; index++)
			if (EventList.get(index).isKeyPress())
				count++;
			
		return count;
	}

	@Override
	public int burstAlphaChars() {
		int index;
		int count = 0;
		for (index = firstKeyPressIndex(); 
				index < lastKeyPressIndex()+1; index++)
			if (EventList.get(index).isKeyPress())
				if (EventList.get(index).isAlpha())
					count++;
			
		return count;
	}

	
	public int burstSpaces() {
		int index;
		int count = 0;
		for (index = firstKeyPressIndex(); 
				index < lastKeyPressIndex()+1; index++)
			if (EventList.get(index).isKeyPress()) {
				if (EventList.get(index).isSpace())
					count++;
			}
		return count;
	}
	
	public int burstTabs() {
		int index;
		int count = 0;
		for (index = firstKeyPressIndex(); 
				index < lastKeyPressIndex()+1; index++)
			if (EventList.get(index).isKeyPress()) {
				if (EventList.get(index).getKeyChar() == '\t')
					count++;
			}
		return count;
	}
	
	public int burstNewLines() {
		int index;
		int count = 0;
		for (index = firstKeyPressIndex(); 
				index < lastKeyPressIndex()+1; index++)
			if (EventList.get(index).isKeyPress()) {
				if (EventList.get(index).getKeyChar() == '\n')
					count++;
			}
		return count;
	}
	
	public int burstPunctuation() {
		int index;
		int count = 0;
		for (index = firstKeyPressIndex(); 
				index < lastKeyPressIndex()+1; index++)
			if (EventList.get(index).isKeyPress()) {
				if (EventList.get(index).isPunctuation())
					count++;
			}
		return count;
	}

	@Override
	public double burstAverageWordLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BurstDelimiter startDelimeter() {
		return bd;
	}

	@Override
	public BurstDelimiter endDelimeter() {
		return bd;
	}

	@Override
	public int burstChars() {
		int index;
		int count = 0;
		for (index = firstKeyPressIndex(); 
				index < lastKeyPressIndex()+1; index++)
			if (EventList.get(index).isKeyPress())
				if (EventList.get(index).isVisible())
					count++;
			
		return count;
	}

	@Override
	public int burstWhiteSpaceChars() {
		int index;
		int count = 0;
		for (index = firstKeyPressIndex(); 
				index < lastKeyPressIndex()+1; index++)
			if (EventList.get(index).isKeyPress()) {
				if (EventList.get(index).isSpace())
					count++;
				if (EventList.get(index).getKeyChar() == '\n')
					count++;
				if (EventList.get(index).getKeyChar() == '\t')
					count++;
			}
		return count;
	}

}
