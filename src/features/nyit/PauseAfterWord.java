/**
 * The package name does not matter but can be used to organize
 * like modules together or to separate teams.
 * 	ex:
 * 		edu.qccuny.module
 * 		edu.latech.module
 * 
 * Package name and class name must be added to 'modules.conf' for
 * them to be loaded.
 */
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
 * This class contains Extraction Module code that detects the Pauses After Words and extracts their durations.
 *
 * Pause duration is defined as the time between press of the last key of the word and the press of the space.
 * @author Raviteja Pokala 
 */
public class PauseAfterWord implements ExtractionModule {
	
	/**
	 * Runs When the Create Template Method is Clicked.
	 * 
	 * @see Answer
	 */
	LinkedList<KeyStroke> wordStack = new LinkedList<KeyStroke>();	
	HashMap<Integer, LinkedList<Long>> featureMap = new HashMap<Integer, LinkedList<Long>>(50);
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		createSearchSpace();
		
		//Iterates over all answers for a user.
		for (Answer a : data) {
			boolean revisionPresent = false;
			long previousKeyPressTime = 0;
			long spacePressTime = 0;
			wordStack.clear();
			Collection<KeyStroke> keys = a.getKeyStrokeList();
			
			//Iterates over all keys of keystroke list.
			for (KeyStroke k : keys) {
				if(k.isKeyPress()) {
					
					if (wordStack.size() > 0)
					//Calculates the pause when the word is complete.
					if (wordStack.peek().isSpace() && k.isAlphaNumeric() && revisionPresent) {
						spacePressTime = wordStack.peek().getWhen();
						int wordlength = popSpaces();
						previousKeyPressTime = wordStack.peek().getWhen();
						if (wordlength < 21) {
							featureMap.get(wordlength).add(spacePressTime - previousKeyPressTime);
						}
						wordStack.clear();
						revisionPresent = false;
					}
				
				
				if(k.isAlphaNumeric())
					wordStack.push(k);
				else if (k.isSpace() && wordStack.size() > 0)
					wordStack.push(k);
				else if (k.isBackspace() && wordStack.size() > 0) {
					wordStack.pop();
					revisionPresent = true;
				}
				
				//else if(kt.isSpace() && kt.isKeyPress() && !revisionPresent && wordStack.size() > 0 || wordStack.size() > 20)
					//wordStack.clear();
			}
		}
		}
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (int i = 1; i < 21; i++)
			output.add(new Feature("P_AWL" + i, featureMap.get(i)));
		return output;
	} 
	
	private void createSearchSpace() {
		wordStack.clear();
		featureMap.clear();
		for (int i = 1; i < 21; i++) {
			featureMap.put(i,new LinkedList<Long>());
		}
	}
	
	private int popSpaces() {
		while (wordStack.peek().isSpace() )
			wordStack.pop();
		return wordStack.size();
	}
	
	/**
	 * Runs when Create Test Vectors is Clicked
	 */
	public String testExtract(DataNode data) {
		// Ignore This For now. Will be deprecated eventually.
		return null;
	}
	
	/**
	 * Returns the Module's Name.
	 */
	public String getName() {
		// A nicely formated String with the module's name.
		return "Pause After Word";
	}
}