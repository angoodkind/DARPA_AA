/**
 * Takes a character stream, and generates all of the corresponding pause times. Returns a visual representation
 * of the stream, with multiple spaces indicating longer pause between words
 */
package extractors.lexical;

import java.util.ArrayList;
import java.util.Collection;

import features.pause.KSE;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

/**
 * @author agoodkind
 *
 */
public class VisualCharStream {
	
	private Collection<KSE> kseArray;
	
	public VisualCharStream(){}
	
	//takes a character stream
	//creates an array of KSEs
	public VisualCharStream(String keystrokes) {
		kseArray = KSE.parseSessionToKSE(keystrokes);
  }
	
	//prints character stream
	public String toString(int pause_threshold) {
		String output = "";
		for (KSE kse: kseArray) {	
			if (kse.isKeyPress()) { //is a down key
				String vkCode = ppVkCode(kse.getKeyCode());
				output+= (pauseLength(kse,pause_threshold)+vkCode);		
			}
		}
		return output;
	}
	
	//determines spacing based on pause length
	public String pauseLength(KSE kse, int pause_threshold) {
		int num_pauses = 0;
		String pauseSpace = "";
		
		if (kse.getM_pauseMs() > pause_threshold) {
			num_pauses = (int) kse.getM_pauseMs()/pause_threshold;
		}
		
		int pauseCount = 0;
		while (pauseCount < num_pauses) {
			pauseSpace += "_";
			pauseCount++;
		}
		
		return pauseSpace;
	}
	
	/**
	 * pretty print a list of KeyTouches using ppVkCode
	 * @param ktList
	 * @return
	 */
	public static String ppVkCode(ArrayList<KeyTouch> ktList) {
		StringBuilder sb = new StringBuilder();
		for (KeyTouch k : ktList) {
			if (k.getKeystroke().isKeyPress()) {
				sb.append(ppVkCode(k.getKeyCode())+" ");
			}
		}
		return sb.length()>0 ? sb.toString().substring(0, sb.length()-1) : sb.toString();
	}
	
	/**
	 * pretty print a list of keystrokes using ppVkCode
	 * @param ksList
	 * @return
	 */
	public static String ppVkCode(Collection<KeyStroke> ksList) {
		StringBuilder sb = new StringBuilder();
		for (KeyStroke k : ksList) {
			if (k.isKeyPress()) {
				sb.append(ppVkCode(k.getKeyCode())+" ");
			}
		}
		return sb.toString();
	}
	
	/** Returns a String of the key name that corresponds to the vkCode.
	 * - slightly altered from original code
	 * <p><p>
	 * If an unknown vkCode-&gt;String-name mapping occurs it will output the String
	 * "Unknown(vkCode)" where vkCode is the integer value of the unknown code.
	 * 
	 * @param vkCode integer vkCode
	 * @return String containing the name of the key.
	*/
	public static String ppVkCode(int vkCode) {
		switch (vkCode) {
		case 8:
			return "[Back]";
		case 9:
			return "[Tab]";
		case 10:
			return "[Enter]";
		case 16:
			return "[Shift]";
		case 17:
			return "[Ctrl]";
		case 18:
			return "[Alt]";
		case 19:
			return "[Break]";
		case 20:
			return "[CapsLock]";
		case 27:
			return "[Esc]";
		case 32:
			return "[Space]";
//			return " ";
		case 33:
			return "[PageUp]";
		case 34:
			return "[PageDown]";
		case 35:
			return "[End]";
		case 36:
			return "[Home]";
		case 37:
			return "[Left]";
		case 38:
			return "[Up]";
		case 39:
			return "[Right]";
		case 40:
			return "[Down]";
		case 44:
			return ",";
		case 45:
			return "-";
		case 46:
			return ".";
		case 47:
			return "/";
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
			return ";";
		case 61:
			return "=";
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
			return "[";
		case 92:
			return "\\";
		case 93:
			return "]";
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
			return "[Delete]";
		case 144:
			return "NumLock";
		case 145:
			return "ScrollLock";
		case 155:
			return "Insert";
		case 192:
			return "GraveAccent";
		case 222:
			return "'";
		case 524:
			return "Windows";
		case 525:
			return "WindowsContext";
		default:
			return "UnknownKey("+ vkCode +")";
		}		
	}
}
