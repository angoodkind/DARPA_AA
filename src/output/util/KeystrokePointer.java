package output.util;

import java.util.*;

import events.EventList;
import features.pause.KSE;
import keystroke.KeyStroke;

public class KeystrokePointer {
	
	protected String finalChar;
	protected LinkedList<KSE> kseList; 
	
	public KeystrokePointer(String finalChar, LinkedList<KSE> kseList) {
		this.finalChar = finalChar;
		this.kseList = kseList;
	}
	
	/**
	 * Can use final text stream as a reference, check each character in the final text stream against 
	 * the keystrokes in the keystroke stream
	 * @param ksListString
	 * @return
	 */
	public static ArrayList<KeystrokePointer> parseKeystrokesToKsp(String ksListString) {
		ArrayList<KeystrokePointer> kspList = new ArrayList<KeystrokePointer>();
		
		Collection<KSE> allKSEs = KSE.parseSessionToKSE(ksListString);
		ArrayList<KSE> kseList = new ArrayList<KSE>();
		for (KSE k : allKSEs) if (k.isKeyPress()) kseList.add(k);
		
		for (int i = 0; i < kseList.size(); i++) {
			
		}
		
		return kspList;
	}

	protected static class IndexedKSE {
		protected KSE kse;
		protected int index;
		
		protected IndexedKSE(KSE kse, int index) {
			this.kse = kse;
			this.index = index;
		}
		
		public KSE getKse() {
			return kse;
		}

		public void setKse(KSE kse) {
			this.kse = kse;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}


	}
}
