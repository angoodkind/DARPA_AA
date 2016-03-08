package keytouch;

import java.awt.event.KeyEvent;
import java.util.*;

import features.pause.KSE;
import keystroke.KeyStroke;

/**
 * Represents an entire key cycle, from press to release
 * For slurs, KeyTouch list is ordered by first key presss 
 * 
 * @author Adam Goodkind
 *
 */
public class KeyTouch {
	
	protected long precedingPause;
	protected boolean inRevision;
	protected long holdTime;
	protected int cursorStart;
	protected int keyCode;
	protected char keyChar;
	protected KeyStroke keystroke;
	
	public KeyTouch() {}
	
	public KeyTouch(KSE pressKSE, KSE releaseKSE) {
		this.precedingPause = pressKSE.getM_pauseMs();
		this.inRevision = pressKSE.isInRevision();
		this.holdTime = releaseKSE.getWhen() - pressKSE.getWhen();
		this.cursorStart = pressKSE.getCursorPosition();
		this.keyCode = pressKSE.getKeyCode();
		this.keyChar = pressKSE.getKeyChar();
		this.keystroke = pressKSE;
	}
	
	/**
	 * This constructor is only used as a workaround when no key release
	 * is found. It creates a 0 hold time, and is not accurate. This is 
	 * done to create data sanity and alignment with keystrokes.
	 * @param pressKSE
	 */
	public KeyTouch(KSE pressKSE) {
		this.precedingPause = pressKSE.getM_pauseMs();
		this.inRevision = pressKSE.isInRevision();
		this.holdTime = (long) 0;
		this.cursorStart = pressKSE.getCursorPosition();
		this.keyCode = pressKSE.getKeyCode();
		this.keyChar = pressKSE.getKeyChar();
		this.keystroke = pressKSE;
	}
	
	/**
	 * Generates a list of KeyTouches
	 * 
	 * @param keystrokes		Use Answer.getKeyStrokes()
	 * @return
	 */
	public static LinkedList<KeyTouch> parseSessionToKeyTouches(String keystrokes) {
		ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(keystrokes));
		LinkedList<KeyTouch> keyTouchList = new LinkedList<KeyTouch>();
		
		for (int i = 0; i < kseList.size(); i++) {
			if (kseList.get(i).isKeyPress()) {
				boolean keyReleaseFound = false;
				for (int j = i+1; j < kseList.size(); j++) { // find the key release
					if (kseList.get(i).getKeyCode()==kseList.get(j).getKeyCode()) { // found it!
						KeyTouch keyTouch = new KeyTouch(kseList.get(i),kseList.get(j));
						keyTouchList.add(keyTouch);
						keyReleaseFound = true;
						break;
					} 
				} if (!keyReleaseFound) { // no key release found. this is rare, but happens
					KeyTouch keyTouch = new KeyTouch(kseList.get(i));
					keyTouchList.add(keyTouch);
				}
			}
		}
		
		return keyTouchList;
	}
	
	/**
	 * TODO: Incorporate backspaces
	 */
	public static LinkedList<KeyTouch> parseToVisibleKeyTouches(LinkedList<KeyTouch> allKTs) {
		LinkedList<KeyTouch> visibleKTs = new LinkedList<KeyTouch>();
		for (KeyTouch kt : allKTs) {
			if (kt.keystroke.isVisible())
				visibleKTs.add(kt);
		}
		return visibleKTs;
	}
	
	/**
	 * Returns vkCode + PrecedingPause + HoldTime + CursorStart
	 */
	public String toString() {
		return KeyStroke.vkCodetoString(this.keyCode)+","+this.precedingPause+","+this.holdTime+","+this.cursorStart;
	}

	public long getPrecedingPause() {
		return precedingPause;
	}

	public void setPrecedingPause(long precedingPause) {
		this.precedingPause = precedingPause;
	}

	public boolean isInRevision() {
		return inRevision;
	}

	public void setInRevision(boolean inRevision) {
		this.inRevision = inRevision;
	}

	public long getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(long holdTime) {
		this.holdTime = holdTime;
	}

	public int getCursorStart() {
		return cursorStart;
	}

	public void setCursorStart(int cursorStart) {
		this.cursorStart = cursorStart;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	public char getKeyChar() {
		return keyChar;
	}

	public void setKeyChar(char keyChar) {
		this.keyChar = keyChar;
	}
	
	public KeyStroke getKeystroke() {
		return keystroke;
	}
	
	
	public static double getElapsedTime(Collection<KeyTouch> keyTouches) {
		double sum = 0.0;
		for (KeyTouch kt : keyTouches) {
			sum += kt.getPrecedingPause();
			sum += kt.getHoldTime();
		}
		return sum;
	}
	
	public static double getElapsedTimeWithoutLeadingPause(List<KeyTouch> keyTouches) {
		if (keyTouches.size() > 0)
			return getElapsedTime(keyTouches) - keyTouches.get(0).getPrecedingPause();
		else
			return Double.NaN;
	}
	
	public static String keyTouchesToString(Collection<KeyTouch> keyTouches) {
		String output = "";
		for (KeyTouch k : keyTouches) {
			if (k.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
				output = output.concat(KeyStroke.vkCodetoString(k.getKeyCode())+"_");
			}
		}
		return output.substring(0, output.length()-1);
	}

	public static VisibleKeystrokeInfo toVisibleKeystrokeInfo(
												LinkedList<KeyTouch> ktList) {
		StringBuilder visibleText = new StringBuilder();
		ArrayList<Integer> visibleKeystrokeIndices = new ArrayList<Integer>();
		
		for (int i=0; i < ktList.size(); i++) {
			KeyStroke k = ktList.get(i).getKeystroke();
			if (k.isKeyPress()) {
				if (k.isBackspace() && visibleText.length()>0) {
					visibleText.deleteCharAt(visibleText.length()-1);
					visibleKeystrokeIndices.remove(visibleKeystrokeIndices.size()-1);
				}
				else if (k.isVisible()) {
					visibleText.append(k.getKeyChar());
					visibleKeystrokeIndices.add(i);
				}
			}
		}
		VisibleKeystrokeInfo info = new VisibleKeystrokeInfo(
				visibleText.toString(),visibleKeystrokeIndices);
		return info;
	}

	
	public static class VisibleKeystrokeInfo {
		protected String visibleText;
		protected ArrayList<Integer> visibleKeystrokeIndices;
		
		VisibleKeystrokeInfo(String visibleText, ArrayList<Integer> visibleKeystrokeIndices) {
			this.visibleText = visibleText;
			this.visibleKeystrokeIndices = visibleKeystrokeIndices;
		}
		
		public String getVisibleText() {
			return visibleText;
		}

		public ArrayList<Integer> getVisibleKeystrokeIndices() {
			return visibleKeystrokeIndices;
		}

	}
}
