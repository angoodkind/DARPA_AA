package keystroke;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import events.EventList;
import events.GenericEvent;

/**
 * KeyStroke represents a key press or key release made by a user in the
 * experiment.
 * KeyStroke extends the awt KeyEvent class and adds extra functionality for use
 * in our experiments.
 * KEY STROKE CLASS
 * 
 * @author Patrick Koch
 * @see java.awt.event.KeyEvent
 */
public class KeyStroke extends KeyEvent implements GenericEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2680655286427301054L;
	public static final char CHAR_UNDEFINED = KeyEvent.CHAR_UNDEFINED;
	public static final int KEY_RELEASED = KeyEvent.KEY_RELEASED;
	private int cursorPosition;
	private final static Container blankContainer = new Container();

	public KeyStroke(int id, long when, int keyCode, char keyChar,
			int cursorPosition) {
		super(blankContainer, id, when, 0, keyCode, keyChar);
		this.cursorPosition = cursorPosition;
	}

	/**
	 * Returns the cursor position at this KeyStroke.
	 * 
	 * @return the cursor position at this KeyStroke.
	 */
	public int getCursorPosition() {
		return cursorPosition;
	}

	/**
	 * Returns a boolean indicating if this is a key press.
	 * 
	 * @return a boolean indicating if this is a key press.
	 */
	public boolean isKeyPress() {
		return (super.getID() == KeyEvent.KEY_PRESSED);
	}

	/**
	 * Returns a boolean indicating if this is a key release.
	 * 
	 * @return a boolean indicating if this is a key release.
	 */
	public boolean isKeyRelease() {
		return (super.getID() == KeyEvent.KEY_RELEASED);
	}

	/**
	 * This returns a Set Object containing vkCodes that are useful to our
	 * study.
	 * <p>
	 * <p>
	 * This lowers the search space and eliminates erroneous key checks.
	 * 
	 * @return Set of vkCodes that are useful in checking during keystroke
	 *         experiments.
	 */
	public static Set<Integer> UsefulVKCodes() {
		TreeSet<Integer> vkSet = new TreeSet<>();
		vkSet.add(8);
		vkSet.add(9);
		vkSet.add(10);
		vkSet.add(16);
		vkSet.add(17);
		vkSet.add(18);
		vkSet.add(19);
		vkSet.add(20);
		vkSet.add(27);
		for (int i = 32; i < 41; i++)
			vkSet.add(i);
		for (int i = 44; i < 58; i++)
			vkSet.add(i);
		vkSet.add(59);
		vkSet.add(61);
		for (int i = 65; i < 94; i++)
			vkSet.add(i);
		for (int i = 96; i < 108; i++)
			vkSet.add(i);
		for (int i = 109; i < 124; i++)
			vkSet.add(i);
		vkSet.add(127);
		vkSet.add(144);
		vkSet.add(145);
		vkSet.add(155);
		vkSet.add(192);
		vkSet.add(222);
		vkSet.add(524);
		vkSet.add(525);
		return vkSet;
	}
	
	/**
	 * This returns a Set Object containing vkCodes that are useful to our
	 * study.
	 * <p/>
	 * <p/>
	 * This lowers the search space and eliminates erroneous key checks.
	 * The reduction was determined by the most frequent unigraphs in training,
	 * each with a count > 1,000 instances
	 *
	 * @return Set of vkCodes that are useful in checking during keystroke
	 * experiments.
	 */
	public static Set<Integer> TopVKCodes() {
		TreeSet<Integer> vkSet = new TreeSet<>();
		vkSet.add(8); //backspace
		//		vkSet.add(9);
		//		vkSet.add(10);
		vkSet.add(16); //shift
		//		vkSet.add(17);
		//		vkSet.add(18);
		//		vkSet.add(19);
		vkSet.add(20); //caps lock
		//		vkSet.add(27);
		vkSet.add(32); //SPACEBAR
		vkSet.add(37); //left arrow
		vkSet.add(39); //right arrow
		vkSet.add(44); //comma
		//		vkSet.add(45); //hyphen
		vkSet.add(46); //period
		for (int i = 65; i < 91; i++) //A-Z (65-91)
			vkSet.add(i);
		//		for (int i = 32; i < 41; i++)
		//			vkSet.add(i);
		//		for (int i = 44; i < 58; i++)
		//			vkSet.add(i);
		//		vkSet.add(59);
		//		vkSet.add(61);
		//		for (int i = 65; i < 94; i++)
		//			vkSet.add(i);
		//		for (int i = 96; i < 108; i++)
		//			vkSet.add(i);
		//		for (int i = 109; i < 124; i++)
		//			vkSet.add(i);
		//		vkSet.add(127);
		//		vkSet.add(144);
		//		vkSet.add(145);
		//		vkSet.add(155);
		//		vkSet.add(192);
		vkSet.add(222); //apostrophe
		//		vkSet.add(524);
		//		vkSet.add(525);
		return vkSet;
	}

	/**
	 * Returns a String of the key name that corresponds to the vkCode.
	 * <p>
	 * <p>
	 * If an unknown vkCode-&gt;String-name mapping occurs it will output the
	 * String "Unknown(vkCode)" where vkCode is the integer value of the unknown
	 * code.
	 * 
	 * @param vkCode
	 *            integer vkCode
	 * @return String containing the name of the key.
	 */
	public static String vkCodetoString(int vkCode) {
		switch (vkCode) {
		case 8:
			return "Backspace";
		case 9:
			return "Tab";
		case 10:
			return "Enter";
		case 16:
			return "Shift";
		case 17:
			return "Ctrl";
		case 18:
			return "Alt";
		case 19:
			return "Break";
		case 20:
			return "CapsLock";
		case 27:
			return "Esc";
		case 32:
			return "Spacebar";
		case 33:
			return "PageUp";
		case 34:
			return "PageDown";
		case 35:
			return "End";
		case 36:
			return "Home";
		case 37:
			return "LeftArrow";
		case 38:
			return "UpArrow";
		case 39:
			return "RightArrow";
		case 40:
			return "DownArrow";
		case 44:
			return "Comma";
		case 45:
			return "Hyphen";
		case 46:
			return "Period";
		case 47:
			return "ForwardSlash";
		case 48:
			return "0";
		case 49:
			return "1";
		case 50:
			return "2";
		case 51:
			return "3";
		case 52:
			return "4";
		case 53:
			return "5";
		case 54:
			return "6";
		case 55:
			return "7";
		case 56:
			return "8";
		case 57:
			return "9";
		case 59:
			return "Semicolon";
		case 61:
			return "Equals";
		case 65:
			return "A";
		case 66:
			return "B";
		case 67:
			return "C";
		case 68:
			return "D";
		case 69:
			return "E";
		case 70:
			return "F";
		case 71:
			return "G";
		case 72:
			return "H";
		case 73:
			return "I";
		case 74:
			return "J";
		case 75:
			return "K";
		case 76:
			return "L";
		case 77:
			return "M";
		case 78:
			return "N";
		case 79:
			return "O";
		case 80:
			return "P";
		case 81:
			return "Q";
		case 82:
			return "R";
		case 83:
			return "S";
		case 84:
			return "T";
		case 85:
			return "U";
		case 86:
			return "V";
		case 87:
			return "W";
		case 88:
			return "X";
		case 89:
			return "Y";
		case 90:
			return "Z";
		case 91:
			return "OpenBracket";
		case 92:
			return "BackSlash";
		case 93:
			return "CloseBracket";
		case 96:
			return "Numpad0";
		case 97:
			return "Numpad1";
		case 98:
			return "Numpad2";
		case 99:
			return "Numpad3";
		case 100:
			return "Numpad4";
		case 101:
			return "Numpad5";
		case 102:
			return "Numpad6";
		case 103:
			return "Numpad7";
		case 104:
			return "Numpad8";
		case 105:
			return "Numpad9";
		case 106:
			return "NumpadMultiply";
		case 107:
			return "NumpadPlus";
		case 109:
			return "NumpadMinus";
		case 110:
			return "NumpadDecimal";
		case 111:
			return "NumpadDivide";
		case 112:
			return "F1";
		case 113:
			return "F2";
		case 114:
			return "F3";
		case 115:
			return "F4";
		case 116:
			return "F5";
		case 117:
			return "F6";
		case 118:
			return "F7";
		case 119:
			return "F8";
		case 120:
			return "F9";
		case 121:
			return "F10";
		case 122:
			return "F11";
		case 123:
			return "F12";
		case 127:
			return "Delete";
		case 144:
			return "NumLock";
		case 145:
			return "ScrollLock";
		case 155:
			return "Insert";
		case 192:
			return "GraveAccent";
		case 222:
			return "SingleQuote";
		case 524:
			return "Windows";
		case 525:
			return "WindowsContext";
		default:
			return "UnknownKey(" + vkCode + ")";
		}
	}

	//char lists used by boolean functions below
	private final List<Character> vowels = Arrays.asList('A', 'E', 'I', 'O',
			'U', 'a', 'e', 'i', 'o', 'u');
	private final List<Character> punctuation = Arrays.asList('.', ',', '?',
			'!', '*', ';', ':', '"', '\'', '/', '[', ']', '{', '}', '(', ')', '-');
	private final List<Character> sentenceFinalPunctuation = Arrays.asList('.','?','!');
	private final List<Character> clauseFinalPunctuation = Arrays.asList(',',';',':');
	private final List<Character> consonants = Arrays.asList('B', 'C', 'D',
			'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
			'V', 'W', 'X', 'Y', 'Z', 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k',
			'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z');
	private final List<Character> symbols = Arrays.asList('@','#','$','%','^','&','*','+','='
			,'|','<','>','`','~');

	/**
	 * Returns true if this key is an alphabetical key event.
	 * 
	 * @return true if this key is an alphabetical key.
	 */
	public boolean isAlpha() {
		if (super.getKeyCode() > 64 && super.getKeyCode() < 91) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if this is a numeric key event.
	 * 
	 * @return true if this is a numeric key event.
	 */
	public boolean isNumeric() {
		if (super.getKeyCode() > 47 && super.getKeyCode() < 58) {
			return true;
		}
		if (super.getKeyCode() > 95 && super.getKeyCode() < 106) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true is this key event is either alphabetical or numeric.
	 * 
	 * @return Returns true is this key event is either alphabetical or numeric.
	 */
	public boolean isAlphaNumeric() {
		return this.isAlpha() || this.isNumeric();
	}

	/**
	 * Returns true is this key event was triggered by the space bar.
	 * 
	 * @return Returns true is this key event was triggered by the space bar.
	 */
	public boolean isSpace() {
		if (super.getKeyCode() == 32) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true is this key event was triggered by the Backspace key.
	 * 
	 * @return Returns true is this key event was triggered by the Backspace
	 *         key.
	 */
	public boolean isBackspace() {
		if (super.getKeyCode() == 8) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if this key event was triggered by a punctuation key
	 * 
	 * @return Returns true if this key event was triggered by a punctuation key
	 */
	public boolean isPunctuation() {
		if (punctuation.contains(super.getKeyChar())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if this key event was triggered by ',' , ';', pr ':'
	 * 
	 * @return Returns true if this key event was triggered by one of above characters
	 */
	public boolean isClauseFinalPunctuation() {
		if (clauseFinalPunctuation.contains(super.getKeyChar())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if this key event was triggered by '.' , '?', or '!'
	 * 
	 * @return Returns true if this key event was triggered by one of above characters
	 */
	public boolean isSentenceFinalPunctuation() {
		if (sentenceFinalPunctuation.contains(super.getKeyChar())) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if this key event is a vowel
	 */
	public boolean isVowel() {
		if (vowels.contains(super.getKeyChar()))
			return true;
		else
			return false;
	}

	/**
	 * Returns true if this key event is a consonant
	 */
	public boolean isConsonant() {
		if (consonants.contains(super.getKeyChar()))
			return true;
		else
			return false;
	}
	
	public boolean isSymbol() {
		if (symbols.contains(super.getKeyChar()))
			return true;
		else
			return false;
	}

	/**
	 * Returns true if keystroke is a visible character
	 * Character check looks for non-unicode characters that
	 * are produced when the Ctrl key is held down
	 * 
	 */
	public boolean isVisible() {
		if (this.isAlphaNumeric() || this.isSpace() || this.isPunctuation() || this.isSymbol()) {
			return true;
		}
		return false;
	}

	/**
	 * Simple Method that returns the character stream from a list of keystroke
	 * objects.
	 * <p/>
	 * Does not handle backspaces or deletes.
	 * 
	 * @param keystrokes
	 *            a list of keystroke events
	 * @return String corresponding to the keystrokes typed.
	 */
	public static String keyStrokesToString(Collection<KeyStroke> keystrokes) {
		String output = "";
		for (KeyStroke k : keystrokes) {
			if (k.isKeyPress() && k.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
				output = output.concat(Character.toString(k.getKeyChar()));
			}
		}
		return output;
	}

	public char getKeyChar() {
		return super.getKeyChar();
	}

	public String toString() {
		return "" + this.isKeyPress() + ":" + super.getKeyCode() + ":"
				+ KeyEvent.getKeyText(super.getKeyCode()) + ":"
				+ this.getWhen() + ":" + this.getCursorPosition();
	}

	/**
	 * Given a Collection of KeyStroke objects this method returns a String that
	 * corresponds to what the user has actually typed on the screen.
	 * 
	 * @param keystrokes
	 *            a Collection of KeyStroke objects
	 * @return String that should match the finalText of a user
	 */
	public static String keyStrokesToFinalText(Collection<KeyStroke> keystrokes) {
		StringBuilder output = new StringBuilder();
		KeyStroke[] keyArray = keystrokes.toArray(new KeyStroke[0]);
		int startIndexOffset = keyArray[0].cursorPosition;
		boolean shiftDown = false;
		SelectionBounds selection = new SelectionBounds();
		for (KeyStroke k : keyArray) {
			shiftDown = processKeyStroke(output, startIndexOffset, shiftDown,
					selection, k);
		}
		return output.toString();
	}

	/**
	 * Processes a single Keystroke into the StringBuffer responsible for
	 * building the final text
	 * 
	 * @param output
	 *            The processing StringBuffer
	 * @param startIndexOffset
	 *            The offset of the first cursor position. This is constant
	 *            through a run
	 * @param shiftDown
	 *            is the shiftkey down?
	 * @param selection
	 *            the bounds on the current selection
	 * @param kt
	 *            the keystroke to process
	 * @return true if the shift key is currently down.
	 */
	public static boolean processKeyStroke(StringBuilder output,
			int startIndexOffset, boolean shiftDown, SelectionBounds selection,
			KeyStroke k) {
		int relativeIndex;
		relativeIndex = k.getCursorPosition() - startIndexOffset;
		if (k.isKeyPress()) {
			if ((relativeIndex < 0) || (relativeIndex > (output.length()))) {
				return shiftDown;
			}
			switch (k.getKeyCode()) {
			case 8: // Backspace
				if (relativeIndex == 0) {
					break;
				}
				if (!selection.isEmpty()) {
					selection.checkOrder();
					output.delete(selection.start, selection.end + 1);
					selection.start = selection.end = -1;
				} else {
					output.deleteCharAt(relativeIndex - 1);
				}
				break;
			case 16: // Shift
				if (selection.isActive()) { // There is already an active
											// selection -- hitting select again
											// shouldn't change anything
					break;
				}
				shiftDown = true; // mark that shift is on and create a new
									// selection
				selection.start = relativeIndex;
				selection.end = relativeIndex;
				break;
			case 127: // Delete
				if (relativeIndex > output.length() - 1) {
					break;
				}
				if (!selection.isEmpty()) {
					selection.checkOrder();
					output.delete(selection.start, selection.end + 1);
				} else {
					output.deleteCharAt(relativeIndex);
				}
				selection.start = selection.end = -1;

				break;
			case VK_LEFT: // 37: // Arrows
				if (!shiftDown) {
					// clear the selection, if not currently shifting.
					selection.start = selection.end = -1;
				} else if (selection.isActive()) {
					selection.end = relativeIndex - 1;
				}
				break;
			case VK_RIGHT: // 39:
				if (!shiftDown) {
					// clear the selection, if not currently shifting.
					selection.start = selection.end = -1;
				} else if (selection.isActive()) {
					selection.end = relativeIndex + 1;
				}
				break;
			case 38:
			case 40:
				if (!shiftDown) {
					// clear the selection, if not currently shifting.
					selection.start = selection.end = -1;
				} else if (selection.isActive()) {
					selection.end = relativeIndex;
				}
				break;
			default:
				if (k.getKeyChar() == 65535) {
					break;
				}
				if (k.getKeyChar() < 9) // nonprinted
				{
					break;
				}
				if (k.getKeyChar() > 11 && k.getKeyChar() < 13) {
					break;
				}
				if (k.getKeyChar() > 13 && k.getKeyChar() < 32) {
					break;
				}
				// Printable characters
				if (selection.isActive() && !selection.isEmpty()) {
					selection.checkOrder();
					output.delete(selection.start, selection.end + 1);
				}
				if (relativeIndex < 0) {
					relativeIndex = 0;
				}
				if (relativeIndex > output.length()) {
					relativeIndex = output.length();
				}
				output.insert(relativeIndex, k.getKeyChar());
				// After a printed character the selection is cleared.
				selection.start = selection.end = -1;
			}
		}
		else if (k.getKeyCode() == 16){ // kt is shift key release
			shiftDown = false;
		}
		return shiftDown;
	}

	public int getKeyCode() {
		return super.getKeyCode();
	}

	public static class SelectionBounds {
		private int start;
		private int end;

		public SelectionBounds() {
			start = -1;
			end = -1;
		}

		private void checkOrder() {
			if (start > end) {
				int tmp = start;
				start = end;
				end = tmp;
			}
		}

		public boolean isEmpty() {
			return start == end;
		}

		public boolean isActive() {
			return (start != -1 && end != -1);
		}
	}

	public int getID() {
		return super.getID();
	}

	public long getWhen() {
		return super.getWhen();
	}
	
	  /**
	   * Given a List of KeyStroke objects this method returns an EventList (extention
	   * of ArrayList) of KeyStrokes that corresponds to what the user has actually 
	   * typed on the screen.
	   *
	   * @param keystrokes a List of KeyStroke objects
	   * @return EventList of KeyStrokes that should match the finalText of a user
	   */
	  public static EventList<KeyStroke> keyStrokesToFinalTextEventList(List<KeyStroke> keystrokes) {
	    EventList<KeyStroke> finalKeyStrokesArray = new EventList<KeyStroke>();
	    KeyStroke[] keyArray = keystrokes.toArray(new KeyStroke[0]);
	    int startIndexOffset = keyArray[0].cursorPosition;
	    boolean shiftDown = false;
	    SelectionBounds selection = new SelectionBounds();
	    for (KeyStroke k : keyArray) {
	      shiftDown = processKeyStroke(finalKeyStrokesArray, startIndexOffset, shiftDown, selection, k);
	    }
	    return finalKeyStrokesArray;
	  }

	  /**
	   * Processes a single Keystroke into the StringBuffer responsible for building the final text
	   *
	   * @param keyStrokeArray   The processing EventList
	   * @param startIndexOffset The offset of the first cursor position. This is constant through a run
	   * @param shiftDown        is the shiftkey down?
	   * @param selection        the bounds on the current selection
	   * @param kt                the keystroke to process
	   * @return true if the shift key is currently down.
	   */
	  public static boolean processKeyStroke(EventList<KeyStroke> keyStrokesArray, int startIndexOffset, boolean shiftDown,
	                                         SelectionBounds selection, KeyStroke k) {
		int relativeIndex;
	    relativeIndex = k.getCursorPosition() - startIndexOffset;
	    if (k.isKeyPress()) {
	      if ((relativeIndex < 0) || (relativeIndex > (keyStrokesArray.size()))) {
	        return shiftDown;
	      }
	      switch (k.getKeyCode()) {
	        case 8:  // Backspace
	          if (relativeIndex == 0) {
	            break;
	          }
	          if (!selection.isEmpty()) {
	            selection.checkOrder();
	            keyStrokesArray.removeAll(selection.start, selection.end);
	            selection.start = selection.end = -1;
	          } else {
	            keyStrokesArray.remove(relativeIndex - 1);
	          }
	          break;
	        case 16:  // Shift
	          if (selection.isActive()) {  // There is already an active selection -- hitting select again shouldn't change anything
	            break;
	          }
	          shiftDown = true;  // mark that shift is on and create a new selection
	          selection.start = relativeIndex;
	          selection.end = relativeIndex;
	          break;
	        case 127:  // Delete
	          if (relativeIndex > keyStrokesArray.size() - 1) {
	            break;
	          }
	          if (!selection.isEmpty()) {
	            selection.checkOrder();
	            keyStrokesArray.removeAll(selection.start, selection.end);
	          } else {
	            keyStrokesArray.remove(relativeIndex);
	          }
	          selection.start = selection.end = -1;

	          break;
	        case VK_LEFT:  // 37:  // Arrows
	          if (!shiftDown) {
	            // clear the selection, if not currently shifting.
	            selection.start = selection.end = -1;
	          } else if (selection.isActive()) {
	            selection.end = relativeIndex - 1;
	          }
	          break;
	        case VK_RIGHT:  // 39:
	          if (!shiftDown) {
	            // clear the selection, if not currently shifting.
	            selection.start = selection.end = -1;
	          } else if (selection.isActive()) {
	            selection.end = relativeIndex + 1;
	          }
	          break;
	        case 38:
	        case 40:
	          if (!shiftDown) {
	            // clear the selection, if not currently shifting.
	            selection.start = selection.end = -1;
	          } else if (selection.isActive()) {
	            selection.end = relativeIndex;
	          }
	          break;
	        default:
	          if (k.getKeyChar() == 65535) {
	            break;
	          }
	          if (k.getKeyChar() < 9)   // nonprinted
	          {
	            break;
	          }
	          if (k.getKeyChar() > 11 && k.getKeyChar() < 13) {
	            break;
	          }
	          if (k.getKeyChar() > 13 && k.getKeyChar() < 32) {
	            break;
	          }
	          // Printable characters
	          if (selection.isActive() && !selection.isEmpty()) {
	            selection.checkOrder();
	            keyStrokesArray.removeAll(selection.start, selection.end);
	          }
	          if (relativeIndex < 0) {
	            relativeIndex = 0;
	          }
	          if (relativeIndex > keyStrokesArray.size()) {
	            relativeIndex = keyStrokesArray.size();
	          }
	          keyStrokesArray.add(relativeIndex, k);
	          // After a printed character the selection is cleared.
	          selection.start = selection.end = -1;
	      }
	    } else if (k.getKeyCode() == 16) { //kt is shift key release
	    	shiftDown = false;
	    }
	    return shiftDown;
	  }
	  
	  /**------------KEYBOARD PROPERTIES------------------**/
	  /**
		 * Returns the Hand ("L" or "R") to which the keystroke
		 * canonically maps, based on the KeyStroke's vkCode
		 * 
		 * Shift, Ctrl, Alt & Space return null
		 * 
		 * @return String containing the hand name ("L" or "R")
		 */
		public String getHand() {
			switch (this.getKeyCode()) {
			case 8: //"Backspace"
				return "R";
			case 9: //"Tab"
				return "L";
			case 10: //"Enter"
				return "R";
			case 16: //"Shift"
				return null;
			case 17: //"Ctrl"
				return null;
			case 18: //"Alt"
				return null;
			case 19: //"Break"
				return "R";
			case 20: //"CapsLock"
				return "L";
			case 27: //"Esc"
				return "L";
			case 32: //"Spacebar"
				return null;
			case 33://"PageUp"
				return "R";
			case 34://"PageDown"
				return "R";
			case 35://"End"
				return "R";
			case 36://"Home"
				return "R";
			case 37://"LeftArrow"
				return "R";
			case 38://"UpArrow"
				return "R";
			case 39://"RightArrow"
				return "R";
			case 40://"DownArrow"
				return "R";
			case 44://"Comma"
				return "R";
			case 45://"Hyphen"
				return "R";
			case 46://"Period"
				return "R";
			case 47://"ForwardSlash"
				return "R";
			case 48://"0"
				return "R";
			case 49://"1"
				return "L";
			case 50://"2"
				return "L";
			case 51://"3"
				return "L";
			case 52://"4"
				return "L";
			case 53://"5"
				return "R";
			case 54://"6"
				return "R";
			case 55://"7"
				return "R";
			case 56://"8"
				return "R";
			case 57://"9"
				return "R";
			case 59://"Semicolon"
				return "R";
			case 61://"Equals"
				return "R";
			case 65://"A"
				return "L";
			case 66://"B"
				return "L";
			case 67://"C"
				return "L";
			case 68://"D"
				return "L";
			case 69://"E"
				return "L";
			case 70://"F"
				return "L";
			case 71://"G"
				return "L";
			case 72://"H"
				return "R";
			case 73://"I"
				return "R";
			case 74://"J"
				return "R";
			case 75://"K"
				return "R";
			case 76://"L"
				return "R";
			case 77://"M"
				return "R";
			case 78://"N"
				return "R";
			case 79://"O"
				return "R";
			case 80://"P"
				return "R";
			case 81://"Q"
				return "L";
			case 82://"R"
				return "L";
			case 83://"S"
				return "L";
			case 84://"T"
				return "L";
			case 85://"U"
				return "R";
			case 86://"V"
				return "L";
			case 87://"W"
				return "L";
			case 88://"X"
				return "L";
			case 89://"Y"
				return "R";
			case 90://"Z"
				return "L";
			case 91://"OpenBracket"
				return "R";
			case 92://"BackSlash"
				return "R";
			case 93://"CloseBracket"
				return "R";
			case 96://"Numpad0"
				return "R";
			case 97://"Numpad1"
				return "R";
			case 98://"Numpad2"
				return "R";
			case 99://"Numpad3"
				return "R";
			case 100://"Numpad4"
				return "R";
			case 101://"Numpad5"
				return "R";
			case 102://"Numpad6"
				return "R";
			case 103://"Numpad7"
				return "R";
			case 104://"Numpad8"
				return "R";
			case 105://"Numpad9"
				return "R";
			case 106://"NumpadMultiply"
				return "R";
			case 107://"NumpadPlus"
				return "R";
			case 109://"NumpadMinus"
				return "R";
			case 110://"NumpadDecimal"
				return "R";
			case 111://"NumpadDivide"
				return "R";
			case 112://"F1"
				return "L";
			case 113://"F2"
				return "L";
			case 114://"F3"
				return "L";
			case 115://"F4"
				return "L";
			case 116://"F5"
				return "L";
			case 117://"F6"
				return "R";
			case 118://"F7"
				return "R";
			case 119://"F8"
				return "R";
			case 120://"F9"
				return "R";
			case 121://"F10"
				return "R";
			case 122://"F11"
				return "R";
			case 123://"F12"
				return "R";
			case 127://"Delete"
				return "R";
			case 144://"NumLock"
				return "R";
			case 145://"ScrollLock"
				return "R";
			case 155://"Insert"
				return "R";
			case 192://"GraveAccent"
				return "L";
			case 222://"SingleQuote"
				return "R";
			case 524://"Windows"
				return null;
			case 525://"WindowsContext"
				return null;
			default:
				return null;
			}
		}
		
		  /**
			 * Returns the numbered finger ("1","2,"3","4","5") to which the keystroke
			 * canonically maps, based on the KeyStroke's vkCode
			 * 
			 * @return String of finger number (1,2,3,4,5)
			 */
			public String getFinger() {
				switch (this.getKeyCode()) {
				case 8: //"Backspace"
					return "5";
				case 9: //"Tab"
					return "5";
				case 10: //"Enter"
					return "5";
				case 16: //"Shift"
					return "5";
				case 17: //"Ctrl"
					return "5";
				case 18: //"Alt"
					return "5";
				case 19: //"Break"
					return "5";
				case 20: //"CapsLock"
					return "5";
				case 27: //"Esc"
					return "5";
				case 32: //"Spacebar"
					return "1";
				case 33://"PageUp"
					return "5";
				case 34://"PageDown"
					return "5";
				case 35://"End"
					return "5";
				case 36://"Home"
					return "5";
				case 37://"LeftArrow"
					return "5";
				case 38://"UpArrow"
					return "5";
				case 39://"RightArrow"
					return "5";
				case 40://"DownArrow"
					return "5";
				case 44://"Comma"
					return "3";
				case 45://"Hyphen"
					return "5";
				case 46://"Period"
					return "4";
				case 47://"ForwardSlash"
					return "5";
				case 48://"0"
					return "5";
				case 49://"1"
					return "5";
				case 50://"2"
					return "4";
				case 51://"3"
					return "3";
				case 52://"4"
					return "2";
				case 53://"5"
					return "2";
				case 54://"6"
					return "2";
				case 55://"7"
					return "2";
				case 56://"8"
					return "3";
				case 57://"9"
					return "4";
				case 59://"Semicolon"
					return "5";
				case 61://"Equals"
					return "5";
				case 65://"A"
					return "5";
				case 66://"B"
					return "2";
				case 67://"C"
					return "3";
				case 68://"D"
					return "4";
				case 69://"E"
					return "3";
				case 70://"F"
					return "2";
				case 71://"G"
					return "2";
				case 72://"H"
					return "2";
				case 73://"I"
					return "3";
				case 74://"J"
					return "2";
				case 75://"K"
					return "3";
				case 76://"L"
					return "4";
				case 77://"M"
					return "2";
				case 78://"N"
					return "2";
				case 79://"O"
					return "4";
				case 80://"P"
					return "5";
				case 81://"Q"
					return "5";
				case 82://"R"
					return "2";
				case 83://"S"
					return "4";
				case 84://"T"
					return "2";
				case 85://"U"
					return "2";
				case 86://"V"
					return "2";
				case 87://"W"
					return "4";
				case 88://"X"
					return "4";
				case 89://"Y"
					return "2";
				case 90://"Z"
					return "5";
				case 91://"OpenBracket"
					return "5";
				case 92://"BackSlash"
					return "5";
				case 93://"CloseBracket"
					return "5";
				case 96://"Numpad0"
					return null;
				case 97://"Numpad1"
					return null;
				case 98://"Numpad2"
					return null;
				case 99://"Numpad3"
					return null;
				case 100://"Numpad4"
					return null;
				case 101://"Numpad5"
					return null;
				case 102://"Numpad6"
					return null;
				case 103://"Numpad7"
					return null;
				case 104://"Numpad8"
					return null;
				case 105://"Numpad9"
					return null;
				case 106://"NumpadMultiply"
					return null;
				case 107://"NumpadPlus"
					return null;
				case 109://"NumpadMinus"
					return null;
				case 110://"NumpadDecimal"
					return null;
				case 111://"NumpadDivide"
					return null;
				case 112://"F1"
					return null;
				case 113://"F2"
					return null;
				case 114://"F3"
					return null;
				case 115://"F4"
					return null;
				case 116://"F5"
					return null;
				case 117://"F6"
					return null;
				case 118://"F7"
					return null;
				case 119://"F8"
					return null;
				case 120://"F9"
					return null;
				case 121://"F10"
					return null;
				case 122://"F11"
					return null;
				case 123://"F12"
					return null;
				case 127://"Delete"
					return "5";
				case 144://"NumLock"
					return null;
				case 145://"ScrollLock"
					return null;
				case 155://"Insert"
					return null;
				case 192://"GraveAccent"
					return "5";
				case 222://"SingleQuote"
					return "5";
				case 524://"Windows"
					return "1";
				case 525://"WindowsContext"
					return null;
				default:
					return null;
				}
			}
	  
			  /**
				 * Returns the row, as outlined below to which the keystroke
				 * canonically maps, based on the KeyStroke's vkCode
				 * num - Number row
				 * top - Top row
				 * hme - Home row
				 * btm - Bottom Row
				 * spc - Space Row
				 * 
				 * @return String of key row
				 */
				public String getRow() {
					switch (this.getKeyCode()) {
					case 8: //"Backspace"
						return "num";
					case 9: //"Tab"
						return "top";
					case 10: //"Enter"
						return "hme";
					case 16: //"Shift"
						return "btm";
					case 17: //"Ctrl"
						return "spc";
					case 18: //"Alt"
						return "spc";
					case 19: //"Break"
						return "num";
					case 20: //"CapsLock"
						return "hme";
					case 27: //"Esc"
						return null;
					case 32: //"Spacebar"
						return "spc";
					case 33://"PageUp"
						return null;
					case 34://"PageDown"
						return null;
					case 35://"End"
						return null;
					case 36://"Home"
						return null;
					case 37://"LeftArrow"
						return "spc";
					case 38://"UpArrow"
						return "spc";
					case 39://"RightArrow"
						return "spc";
					case 40://"DownArrow"
						return "spc";
					case 44://"Comma"
						return "btm";
					case 45://"Hyphen"
						return "num";
					case 46://"Period"
						return "btm";
					case 47://"ForwardSlash"
						return "btm";
					case 48://"0"
						return "num";
					case 49://"1"
						return "num";
					case 50://"2"
						return "num";
					case 51://"3"
						return "num";
					case 52://"4"
						return "num";
					case 53://"5"
						return "num";
					case 54://"6"
						return "num";
					case 55://"7"
						return "num";
					case 56://"8"
						return "num";
					case 57://"9"
						return "num";
					case 59://"Semicolon"
						return "hme";
					case 61://"Equals"
						return "num";
					case 65://"A"
						return "hme";
					case 66://"btm"
						return "btm";
					case 67://"C"
						return "btm";
					case 68://"D"
						return "hme";
					case 69://"E"
						return "top";
					case 70://"F"
						return "hme";
					case 71://"G"
						return "hme";
					case 72://"hme"
						return "hme";
					case 73://"I"
						return "top";
					case 74://"J"
						return "hme";
					case 75://"K"
						return "hme";
					case 76://"L"
						return "hme";
					case 77://"M"
						return "btm";
					case 78://"num"
						return "btm";
					case 79://"O"
						return "top";
					case 80://"P"
						return "top";
					case 81://"Q"
						return "top";
					case 82://"R"
						return "top";
					case 83://"spc"
						return "hme";
					case 84://"top"
						return "top";
					case 85://"U"
						return "top";
					case 86://"V"
						return "btm";
					case 87://"W"
						return "top";
					case 88://"X"
						return "btm";
					case 89://"Y"
						return "top";
					case 90://"Z"
						return "btm";
					case 91://"OpenBracket"
						return "top";
					case 92://"BackSlash"
						return "top";
					case 93://"CloseBracket"
						return "top";
					case 96://"Numpad0"
						return null;
					case 97://"Numpad1"
						return null;
					case 98://"Numpad2"
						return null;
					case 99://"Numpad3"
						return null;
					case 100://"Numpad4"
						return null;
					case 101://"Numpad5"
						return null;
					case 102://"Numpad6"
						return null;
					case 103://"Numpad7"
						return null;
					case 104://"Numpad8"
						return null;
					case 105://"Numpad9"
						return null;
					case 106://"NumpadMultiply"
						return null;
					case 107://"NumpadPlus"
						return null;
					case 109://"NumpadMinus"
						return null;
					case 110://"NumpadDecimal"
						return null;
					case 111://"NumpadDivide"
						return null;
					case 112://"F1"
						return null;
					case 113://"F2"
						return null;
					case 114://"F3"
						return null;
					case 115://"F4"
						return null;
					case 116://"F5"
						return null;
					case 117://"F6"
						return null;
					case 118://"F7"
						return null;
					case 119://"F8"
						return null;
					case 120://"F9"
						return null;
					case 121://"F10"
						return null;
					case 122://"F11"
						return null;
					case 123://"F12"
						return null;
					case 127://"Delete"
						return "num";
					case 144://"NumLock"
						return null;
					case 145://"ScrollLock"
						return null;
					case 155://"Insert"
						return null;
					case 192://"GraveAccent"
						return "num";
					case 222://"SingleQuote"
						return "hme";
					case 524://"Windows"
						return "spc";
					case 525://"WindowsContext"
						return "spc";
					default:
						return null;
					}
				}
}