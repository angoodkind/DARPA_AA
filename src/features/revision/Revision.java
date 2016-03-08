package features.revision;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import events.EventList;
import extractors.lexical.VisualCharStream;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

/**
 * This class parses a list of KeyTouches into a list of Revisions. A Revision
 * consists of the deleted keystrokes and the keystrokes it was replaced with.
 * 
 * The class RevisionCSV will produce a CSV of each Revision and the properties of
 * the user and Revision itself
 * 
 * @author Adam Goodkind
 *
 */
public class Revision {

	protected static HashMap<Character,HashSet<Character>> keyNeighbors; 
	private static final String keys = "qwertyuiopasdfghjkl;zxcvbnm,.";

	protected static HashSet<String> wordDictionary;

	protected ArrayList<KeyTouch> deletedKeyTouches;
	protected ArrayList<KeyTouch> revisedKeyTouches;
	protected ArrayList<KeyTouch>  backspaceKeyTouches;
	protected int startingIndex;
	protected int editDistance;

	public Revision(ArrayList<KeyTouch> deletedKeyTouches, ArrayList<KeyTouch> revisedKeyTouches, 
			ArrayList<KeyTouch> backspaceKeyTouches, int startingIndex) {
		this.deletedKeyTouches = deletedKeyTouches;
		this.revisedKeyTouches = revisedKeyTouches;
		this.backspaceKeyTouches = backspaceKeyTouches;
		this.startingIndex = startingIndex;
		this.editDistance = calculateEditDistance(deletedKeyTouches, revisedKeyTouches);
	}

	/**
	 * 
	 * Calculates minimum edit (Levenshtein) distances for deleted
	 * versus revised text
	 * 
	 * If the deleted text is shorter than or equal in length to the revised
	 * text, then the comparison is done using the length of the deleted text.
	 * If the revised text is shorter, then the full texts are compared
	 * 
	 * @param deletedKTs
	 * @param revisedKTs
	 * @return
	 */
    public static int calculateEditDistance(ArrayList<KeyTouch> deletedKTs, ArrayList<KeyTouch> revisedKTs) {
        String deletedStr = KeyTouch.keyTouchesToString(deletedKTs).toLowerCase();
        String revisedStr = KeyTouch.keyTouchesToString(revisedKTs).toLowerCase();
//        System.out.format("%d,  %d, ", deletedStr.length(), revisedStr.length());
        boolean shortenRevision = revisedStr.length() < deletedStr.length()? true : false;
        revisedStr = revisedStr.substring(0, 
        		shortenRevision? revisedStr.length() : deletedStr.length());
//        System.out.format("%d \n", revisedStr.length());
        // i == 0
        int [] costs = new int [revisedStr.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= deletedStr.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= revisedStr.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), deletedStr.charAt(i - 1) == revisedStr.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[revisedStr.length()];
    }
	/**
	 * If the deleted keystrokes countain a doubtlet, and the
	 * revised keystrokes contain a *different*  doublet, this
	 * is a doubling error, e.g. schhol -> school.
	 * @return true if doubling error
	 */
	public boolean isDoublingError() {
		if (deletedKeyTouches.size() >= 2 && revisedKeyTouches.size() >= 2) {
			// search deleted keystrokes for a doubtlet
			for (int i=0; i < deletedKeyTouches.size()-1; i++) {
				// found a doubtlet
				if (deletedKeyTouches.get(i).getKeyCode() == deletedKeyTouches.get(i+1).getKeyCode()) {
					// search revised keystrokes for a doublet
					for (int j=0; j < revisedKeyTouches.size()-1; j++) {
						// found a doubtlet
						if (revisedKeyTouches.get(j).getKeyCode() == revisedKeyTouches.get(j+1).getKeyCode()) {
							// make sure doublets are different
							if (deletedKeyTouches.get(i).getKeyCode() != revisedKeyTouches.get(j).getKeyCode())
								return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * If one keystroke is deleted, and then placed two to four
	 * characters later in the revised text. Presumably, this error
	 * was caused by a misfiiring, where a letter was executed a few
	 * characters too early.
	 * @return
	 */
	public boolean isEarlyFinger() {
		if (this.deletedKeyTouches.size()==1) {
			if (this.revisedKeyTouches.size() > 1) {
				int i = 1;
				int deletedVK = deletedKeyTouches.get(0).getKeyCode();
				while (i < 4 && i < revisedKeyTouches.size()) {
					if (deletedVK == revisedKeyTouches.get(i).getKeyCode()) {
						return true;
					}
					i++;
				}
			}
		}
		return false;
	}

	/**
	 * TODO: first try with one letter replacements, then look further back
	 * @return
	 */
	public boolean isFatFinger() {
		// initialize key neighbor map if first time running
		if (keyNeighbors==null) {
			keyNeighbors = new HashMap<Character,HashSet<Character>>();
			createKeyMap();
		}

		if (this.size() == 1 && this.revisedKeyTouches.size()>0) {
			char delKeyChar = this.deletedKeyTouches.get(0).getKeyChar();
			if (keyNeighbors.keySet().contains(delKeyChar)) {
				HashSet<Character> neighbors = keyNeighbors.get(delKeyChar);
				char revKeyChar = this.revisedKeyTouches.get(0).getKeyChar();
				if (neighbors.contains(revKeyChar)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if revised keystrokes are the same keys as the deleted 
	 * keystrokes. Order is not important
	 * @return
	 */
	public boolean isTransposition() {
		ArrayList<Integer> delVKs = new ArrayList<Integer>();
		ArrayList<Integer> revVKs = new ArrayList<Integer>();
		int dLen = deletedKeyTouches.size();
		int rLen = revisedKeyTouches.size();

		for (int i = 0; i < deletedKeyTouches.size(); i++) {
			if (deletedKeyTouches.get(i).getKeystroke().isAlphaNumeric())
				delVKs.add(this.deletedKeyTouches.get(i).getKeyCode());
		}
		// only look at the number of characters in the revision equal
		// to the number of characters in the deleted text +1
		// if deleted is longer than revised, use revised 
		for (int i = 0; i < (dLen+1>rLen? rLen : dLen+1); i++) {
			if (revisedKeyTouches.get(i).getKeystroke().isAlphaNumeric())
				revVKs.add(this.revisedKeyTouches.get(i).getKeyCode());
		}
		// in order to be a transposition, need at least 2 letters
		if (delVKs.size()>1) {
			// make sure deletion and revision are not the same
			if (!this.isUnchanged())
				return revVKs.containsAll(delVKs);
		}
		return false;
	}
	/**
	 * checks if deleted text was replaced with the same text
	 * @return
	 */
	public boolean isUnchanged() {
		ArrayList<Integer> delVKs = new ArrayList<Integer>();
		ArrayList<Integer> revVKs = new ArrayList<Integer>();

		// if last deleted charcter is a space, do not count it, since 
		// spaces are not included in revised text
		int tempSize = deletedKeyTouches.get(deletedKeyTouches.size()-1).getKeyCode()==32?
				deletedKeyTouches.size()-1 : deletedKeyTouches.size();

				if (revisedKeyTouches.size() < deletedKeyTouches.size())
					return false;
				for (int i = 0; i < tempSize; i++) {
					delVKs.add(this.deletedKeyTouches.get(i).getKeyCode());
					revVKs.add(this.revisedKeyTouches.get(i).getKeyCode());
				}
				// check for equality and that deletion was not just a space, which
				// would get deleted above
				return (delVKs.equals(revVKs) && delVKs.size()>0);
	}
	/**
	 * if revision is a completely different lexical choice, e.g. "baseball"
	 * becomes "football" and both are complete words
	 * @return
	 */
	public boolean isWordChange() {
		if (wordDictionary == null) {
			wordDictionary = new HashSet<String>();
			loadDictionary("sae-sorted.dic");
		}
		if (!this.isUnchanged()) {
			if (wordDictionary.contains(
					KeyTouch.keyTouchesToString(this.deletedKeyTouches).toLowerCase())
					&&
					wordDictionary.contains(
							KeyTouch.keyTouchesToString(this.revisedKeyTouches).toLowerCase()))
				return true;
		}
		return false;
	}
	
	public double getPauseBeforeBackspaces() {
		return this.deletedKeyTouches.get(0).getPrecedingPause();
	}
	
	public double getPauseBeforeRevisedText() {
		if (this.revisedKeyTouches.size() > 0)
			return this.revisedKeyTouches.get(0).getPrecedingPause();
		else
			return Double.NaN;
	}
	
	
	public double getMeanBackspacingHold() {
		double sum = 0.0;
		for (KeyTouch kt : this.backspaceKeyTouches) {
			sum += kt.getHoldTime();
		}
		return sum/this.size();
	}
	
	public double getMeanBackspacingPause() {
		double sum = 0.0;
		for (KeyTouch kt : this.backspaceKeyTouches) {
			sum += kt.getPrecedingPause();
		}
		return sum/this.size();
	}
	
	
	/**
	 * String representation of Revisions <p>
	 * Prints "deleted text| revised text| starting index"
	 */
	@Override
	public String toString() {
		return String.format("%s|%s|%d|%d", 
				VisualCharStream.ppVkCode(this.deletedKeyTouches),
				VisualCharStream.ppVkCode(this.revisedKeyTouches), 
				this.startingIndex,
				this.size());
	}

	/**
	 * A revision's size is defined as the number of characters replaced
	 * @return The number of KeyTouches in the revision
	 */
	public int size() {
		return this.backspaceKeyTouches.size();
	}
	
	public int getEditDistance() {
		return editDistance;
	}

	public void setEditDistance(int editDistance) {
		this.editDistance = editDistance;
	}
	/**
	 * Parse a list of KeyStrokes into a list of Revisions
	 * @param allKsList
	 * @return a list of Revisions
	 */
	public static ArrayList<Revision> parseKeystrokesToRevision(LinkedList<KeyTouch> ktList) {
		//create Revision objects from list of key presses
		ArrayList<Revision> revisionList = new ArrayList<Revision>();
		int backspaceCount = 0;
		boolean revising = false;
		for (int i = 0; i < ktList.size(); i++) {
			//System.out.println(i+" "+ktList.get(i).getKeyChar()+" "+backspaceCount+" "+revising+" ");
			if (ktList.get(i).getKeystroke().isBackspace()) {
				backspaceCount++;
				revising = true;
			}
			else if (revising) { //end of revision (not a backspace but currently in revising)
				ArrayList<KeyTouch> deletedKeyTouches = new ArrayList<KeyTouch>();
				ArrayList<KeyTouch> revisedKeyTouches = new ArrayList<KeyTouch>();
				ArrayList<KeyTouch> backspaceKeyTouches = new ArrayList<KeyTouch>();

				//add backspaces
				int backspacesAdded = 0;
				while (backspacesAdded < backspaceCount) {
					KeyTouch backK = ktList.get(i-backspacesAdded-1);
					//System.out.println(backK.getKeyChar()+"|");
					backspaceKeyTouches.add(backK);
					backspacesAdded++;
				}

				//add deleted keystrokes
				int visibleKeysDeleted = 0;
				int iDelOffset = i - backspaceCount - 1;
				while (visibleKeysDeleted < backspaceCount && iDelOffset > 0) {
					KeyTouch delK = ktList.get(iDelOffset);
					deletedKeyTouches.add(0,delK);
					if (delK.getKeystroke().isVisible())
						visibleKeysDeleted++;
					iDelOffset--;
				}

				// add revised keystrokes
				// a "revision" goes until the end of the word
				int iRevOffset = i;
				while (iRevOffset < ktList.size() && !ktList.get(iRevOffset).getKeystroke().isSpace()) {
					KeyTouch revK = ktList.get(iRevOffset);
					// embedded revision
					// add backspaces to backspaceKeyTouches
					// move deleted KeyTouch from revisedKeyTouches to deletedKeyTouches
					if (revK.getKeystroke().isBackspace()) {
						backspaceKeyTouches.add(revK);
						if (revisedKeyTouches.size()>0) {
							KeyTouch moveKeyTouch = revisedKeyTouches.get(revisedKeyTouches.size()-1);
							revisedKeyTouches.remove(revisedKeyTouches.size()-1);
							deletedKeyTouches.add(moveKeyTouch);
						}
					}
					else {
						revisedKeyTouches.add(revK);
					}
					++iRevOffset;
				}

				//calculate start index based on backspaces and deleted keystrokes
				// if formula for startIdx makes it less than 0 set startIdx to 0
				// and set start index to i, which is the real buffer position
				int startIdx;
				if (i - (backspaceKeyTouches.size() + deletedKeyTouches.size()) < 0) {
					startIdx = 0;
				}
				else {
					startIdx = i - (backspaceKeyTouches.size() + deletedKeyTouches.size());
				}
				//calculate i offset based on revised keys added
				i = iRevOffset - 1;
				//					System.out.format("%d offset\n",i);

				//					System.out.println(KeyTouch.keyTouchesToString(deletedKeyTouches)+" "+
				//										KeyTouch.keyTouchesToString(revisedKeyTouches)+" "+
				//										startIdx);
				Revision r = new Revision(
						deletedKeyTouches,revisedKeyTouches,backspaceKeyTouches,startIdx);
				if (deletedKeyTouches.size()!=0)
					revisionList.add(r);
				//				System.out.println(r);

				revising = false;
				backspaceCount = 0;
			} // end "if revising" loop	
		}
		return revisionList;	
	}


	public static ArrayList<Integer> getRevStartingIdxList(ArrayList<Revision> revList) {
		ArrayList<Integer> revStartingIdxList = new ArrayList<Integer>();
		for (Revision r : revList)
			revStartingIdxList.add(r.startingIndex);
		return revStartingIdxList;
	}

	public static ArrayList<KeystrokeRevision> extendKeystrokesToRevisions(LinkedList<KeyTouch> ktList) {
		ArrayList<KeystrokeRevision> keyRevList = new ArrayList<KeystrokeRevision>();
		// create Revision List
		ArrayList<Revision> revList = parseKeystrokesToRevision(ktList);
		// create index list
		ArrayList<Integer> idxList = getRevStartingIdxList(revList);
		// go through keystrokes, checking if index is in index list
		for (int i = 0; i < ktList.size(); i++) {
			// if this i begins a revision
			if (idxList.contains(i)) {
				Revision r = revList.get(idxList.indexOf(i));
				// unpack Revision elements
				for (KeyTouch delK : r.deletedKeyTouches) {
					//System.out.println(delK.getKeyChar()+" del");
					KeystrokeRevision kr = new KeystrokeRevision(delK,"Deletion");
					keyRevList.add(kr);
				}
				for (KeyTouch backK : r.backspaceKeyTouches) {
					//System.out.println(backK.getKeyChar()+" back");
					KeystrokeRevision kr = new KeystrokeRevision(backK,"Backspace");
					keyRevList.add(kr);
				}
				for (KeyTouch revK : r.revisedKeyTouches) {
					//System.out.println(revK.getKeyChar()+" rev");
					KeystrokeRevision kr = new KeystrokeRevision(revK, "Replacement");
					keyRevList.add(kr);
				}
				i = (i + r.size()) - 1; // -1 offset since i will be incremented by loop
			}
			else { // if not, make a non-rev KR
				KeyTouch kt = ktList.get(i);
				KeystrokeRevision kr = new KeystrokeRevision(kt,"NonRevision");
				keyRevList.add(kr);
			}
		}
		return keyRevList;
	}

	/**
	 * An extension of KeyTouches, that also notes the revision
	 * status of the KeyTouch.<p>
	 * Options for revision status:<br>
	 * - "Deletion"<br>
	 * - "Replacement"<br>
	 * - "NonRevision"<br>
	 * 
	 * @author Adam Goodkind
	 *
	 */
	protected static class KeystrokeRevision {
		KeyTouch kt;
		RevisionStatus revStatus;

		KeystrokeRevision(KeyTouch k) {
			this.kt = k;
		}

		KeystrokeRevision(KeyTouch k, String revStatus) {
			this.kt = k;
			this.revStatus = RevisionStatus.valueOf(revStatus);
		}

		protected void setRevisionStatus(String revStatus) {
			this.revStatus = RevisionStatus.valueOf(revStatus);
		}

		protected KeyTouch getKeyTouch() {
			return this.kt;
		}

		protected RevisionStatus getRevisionStatus() {
			return this.revStatus;
		}

		protected boolean isDeletion() {
			return this.revStatus == RevisionStatus.Deletion;
		}

		protected boolean isReplacement() {
			return this.revStatus == RevisionStatus.Replacement;
		}

		protected boolean isNonRevision() {
			return this.revStatus == RevisionStatus.NonRevision;
		}

		protected enum RevisionStatus {
			Deletion, Replacement, NonRevision, Backspace
		}

		/**
		 * output as vkCodeToString(keystroke)_revisionStatus<br>
		 */
		@Override
		public String toString() {
			return (KeyStroke.vkCodetoString(this.kt.getKeyCode())+"_"+this.revStatus);
		}
	}

	/**
	 * Changes the argument to lower case and adds it to the dictionary
	 * @param word a string to be added
	 * @return true if word added correctly; false otherwise.
	 */
	protected boolean addWord (String word) {
		try {
			return wordDictionary.add(word.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Reads the argument as the name of the file to be loaded.
	 * File must have one (dictionary) word per line.
	 * @param filename name of file to load.
	 * @return true if loaded successfully; false otherwise.
	 */
	private boolean loadDictionary(String filename) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String         line = null;
			while ((line = reader.readLine()) != null) 
				if (! addWord(line)) {
					reader.close();
					throw new Exception("Failed to add word " + line);
				}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//============= Utilities for key mapping ======================//	
	private static void createKeyMap() {
		String keys = "qwertyuiopasdfghjkl;zxcvbnm,.";
		for (char k : keys.toCharArray()) {
			HashSet<Character> kNeighbors = new HashSet<Character>();
			for (char kNeighbor : keys.toCharArray()) {
				if (k!=kNeighbor && keyboardDistance(k,kNeighbor)<2) {
					kNeighbors.add(kNeighbor);
				}
			}
			keyNeighbors.put(k, kNeighbors);
		}
	}

	private static double keyboardDistance(char c1, char c2) {
		return Math.sqrt(Math.pow(colOf(c2)-colOf(c1),2)+Math.pow(rowOf(c2)-rowOf(c1),2));
	}

	private static int rowOf(char c) {
		return keys.indexOf(c) / 10;
	}

	private static int colOf(char c) {
		return keys.indexOf(c) % 10;
	}
	//====================================================//

}
