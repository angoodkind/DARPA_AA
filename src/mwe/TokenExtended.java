package mwe;

import java.util.ArrayList;

import extractors.lexical.TokenExtender.IndexInfo;
import extractors.lexical.TokenExtender.TokenSpan;
import features.pause.KSE;
import keytouch.KeyTouch;

/**
 * Holds all fields that are necessary for performing timing extraction
 * within a word token
 * <p><p>
 * The TokenSpan indices can be used to align an array of TokenExtendeds
 * to an Array of KSEs. To do so, create an array of visible KSEs using
 * KSE.parseToVisibleTextKSEs(). Then create a visible text string using
 * KeyStroke.toVisibleTextString(). Use this string to create the TokenExtendeds.
 * Once this is complete, the TokenSpan of a TokenExtended will correspond to
 * the indices of the visible KSE list that makes up the token. An example is 
 * provided in the unit test for TokenExtended.
 * @author Adam Goodkind
 *
 */
public class TokenExtended {
	public final String token;
	public final String partOfSpeech;
	public final String lemma;
	public final TokenSpan tokenSpan;
	public final TokenSpan rawKeyTouchIndices;
	public final IndexInfo indexInfo;
	public final ArrayList<KeyTouch> keyTouchList;
	
	/**
	 * This constructor does not include raw keystroke indices
	 */
	@Deprecated
	public TokenExtended(String token, String partOfSpeech, String lemma, TokenSpan tokenSpan) {
		this.token = token;
		this.partOfSpeech = partOfSpeech;
		this.lemma = lemma;
		this.tokenSpan = tokenSpan;
		this.indexInfo = null;
		this.rawKeyTouchIndices = null;
		this.keyTouchList = null;
	}

	public TokenExtended(String token, String partOfSpeech, String lemma, TokenSpan tokenSpan,
						TokenSpan rawKeystrokeIndices, IndexInfo indexInfo, ArrayList<KeyTouch> keyTouchList) {
		this.token = token;
		this.partOfSpeech = partOfSpeech;
		this.lemma = lemma;
		this.tokenSpan = tokenSpan;
		this.rawKeyTouchIndices = rawKeystrokeIndices;
		this.indexInfo = indexInfo;
		this.keyTouchList = keyTouchList;
	}

	public String getToken() {
		return token;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public String getLemma() {
		return lemma;
	}
	
	public TokenSpan getTokenSpan() {
		return tokenSpan;
	}
	
	public TokenSpan getRawKeyTouchIndices() {
		return rawKeyTouchIndices;
	}
	
	public ArrayList<KeyTouch> getKeyTouchList() {
		return this.keyTouchList;
	}
	
	/**
	 * pretty prints KeyTouchList
	 * @return a string of key characters
	 */
	public String printKeyTouchList() {
		return KeyTouch.keyTouchesToString(this.keyTouchList);
	}
	/**
	 * If any of the keystrokes within the token are Backspace or Delete,
	 * return true.
	 */
	public boolean containsRevision() {
		for (KeyTouch k : this.keyTouchList) {
			if (k.getKeystroke().isBackspace() || k.getKeyCode()==127/**delete**/)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the number of Backspace and Delete KeyTouches within
	 * the Token
	 */
	public int revisionCount() {
		int revisionCount = 0;
		for (KeyTouch k : this.keyTouchList) {
			if (k.getKeystroke().isBackspace() || k.getKeyCode()==127/**delete**/)
				revisionCount++;
		}
		return revisionCount;
	}
	
	public int size() {
		return (this.tokenSpan.end-this.tokenSpan.begin);
	}
	
	public String toString() {
		return (this.token+" "+this.partOfSpeech+" "+this.lemma+" "+KeyTouch.keyTouchesToString(this.keyTouchList)+" "
				+this.tokenSpan.toString()+" "+this.rawKeyTouchIndices.toString());
	}

}
