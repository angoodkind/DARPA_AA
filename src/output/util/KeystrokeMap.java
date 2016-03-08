package output.util;

import java.util.*;

import events.EventList;
import features.pause.KSE;
import keystroke.KeyStroke;

/**
 * Create two maps, one to map keystroke (KSEs) indices to final text, and
 * one to map final text indices to a list of constituent keystroke (KSEs) indices
 * <p><p>
 * The class then includes a lookup function to access the underlying KSEs and 
 * characters
 * 
 * @author Adam Goodkind
 *
 */
public class KeystrokeMap {
	protected ArrayList<KSE> kseList;
	protected String finalText;
	protected Map<Integer,Integer> kseToText;
	protected Map<Integer,ArrayList<Integer>> textToKSEs;
	
	public KeystrokeMap(ArrayList<KSE> kseList, String finalText) {
		this.kseList = kseList;
		this.finalText = finalText;
		kseToText = new HashMap<Integer,Integer>();
		textToKSEs = new HashMap<Integer,ArrayList<Integer>>();
	}
	
	public static KeystrokeMap parseKeystrokesToMap(String keystrokeStr, String finalTextStr) {
		ArrayList<KSE> keyPresses = KSE.parseToKeyPressKSEs(keystrokeStr);
		KeystrokeMap ksMap = new KeystrokeMap(keyPresses,finalTextStr);
		ArrayList<Integer> backspaceIdxs = indexOfAll(8, keyPresses);
		
		
		
		
		return ksMap;
	}
	
	private static ArrayList<Integer> indexOfAll(int vkCode, ArrayList<KSE> list){
	    ArrayList<Integer> indexList = new ArrayList<Integer>();
	    for (int i = 0; i < list.size(); i++)
	        if(list.get(i).getKeyCode() == vkCode)
	            indexList.add(i);
	    return indexList;
	}
}
