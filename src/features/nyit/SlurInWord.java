package features.nyit;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

/**
 * Detects and logs occurrences of slurs in N-letter words.
 * <br><br>
 * Reports These occurrences as 1's in the template and are ordered by word length.
 * <br><br>
 * word length is defined as the length of the word AFTER a revision.
 * 
 * @author Patrick
 */
public class SlurInWord implements ExtractionModule {
		
	LinkedList<KeyStroke> keyStack = new LinkedList<KeyStroke>();
	
	HashMap<Integer, LinkedList<Integer>> featureMap = new HashMap<Integer, LinkedList<Integer>>(80); 
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		//Initialize module
		init();
		
		for (Answer a : data) {
			//Create and initialize temporary variables.
			KeyStroke lastKeyEvent = new KeyStroke(KeyStroke.KEY_RELEASED,  0L, 0, '0', 0);
			boolean slurDetected = false;
			keyStack.clear();
			for (KeyStroke k : a.getKeyStrokeList()) {
				
				
				if (k.isKeyPress()) {
					//Detects Slur based on sequential key presses of different keys.
					if (lastKeyEvent.isKeyPress() && lastKeyEvent.isAlphaNumeric()
							&& lastKeyEvent.getKeyCode() != k.getKeyCode()
							&& k.isAlphaNumeric() ) {
						slurDetected = true;
					}
					
					
					if (keyStack.size() > 0)
						//if word is completed and next word has begun
						if (keyStack.peek().isSpace() 
								&& k.isAlphaNumeric()) {
							int wordlength = popSpaces();
							if (slurDetected && wordlength < 21) {
								featureMap.get(wordlength).add(1);
							}
							keyStack.clear();
							slurDetected = false;
						}
					
					//Push if key is alphanumeric
					if (k.isAlphaNumeric())
						keyStack.push(k);
					//push spaces if stack is not empty
					else if (k.isSpace() && keyStack.size() > 0)
						keyStack.push(k);
					//pop stack if incoming key is backspace and stack is not empty
					else if (k.isBackspace() && keyStack.size() > 0) {
						keyStack.pop();
						//If the word is completely deleted reset slur flag
						if (keyStack.size() == 0)
							slurDetected = false;
					}
						
				}
				//logs the last key event regardless of it being a press or release.
				lastKeyEvent = k;
			}
		}
		
		//Format output for extractor
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (int i = 1; i < 21; i++) {
			output.add(new Feature("SIW_L" + i,  featureMap.get(i)));
		}
		return output;
	}

	@Override
	public String getName() {
		return "Slur In N-Letter Word";
	}
	
	/**
	 * Resets the module for a fresh pass.
	 */
	private void init() {
		keyStack.clear();
		featureMap.clear();
		for (int i = 1; i < 21; i++) {
			featureMap.put(i,new LinkedList<Integer>());
		}
	}
	
	/**
	 * pops all spaces from the stack and then reports the size remaining
	 * 
	 * @return int size of stack
	 */
	private int popSpaces() {
		while (keyStack.peek().isSpace() )
			keyStack.pop();
		return keyStack.size();
	}
}
