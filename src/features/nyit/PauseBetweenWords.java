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
public class PauseBetweenWords implements ExtractionModule {
	
	/**
	 * Runs When the Create Template Method is Clicked.
	 * 
	 * @see Answer
	 */
	LinkedList<KeyStroke> firstWord = new LinkedList<KeyStroke>();
	LinkedList<KeyStroke> secondWord = new LinkedList<KeyStroke>();
	
	HashMap<Integer, LinkedList<Long>> featureMap = new HashMap<Integer, LinkedList<Long>>(50);
	
	@Override
	public Collection<Feature> extract(DataNode data) throws NullPointerException {
		createSearchSpace();
		
		//Iterates over all answers for a user.
		for (Answer a : data) {
			
			boolean backspace_present = false;
			boolean previous_word = false;
			firstWord.clear();
			secondWord.clear();
			Collection<KeyStroke> keys = a.getKeyStrokeList();
			
			//Iterates over all keys of keystroke list.
			for (KeyStroke k : keys) {
				if(k.isKeyPress()) {
					
					if (firstWord.size() > 0)
						//if word is completed and next word has begun
						if (firstWord.peek().isSpace() && k.isAlphaNumeric()) {
							int wordLength = popSpaces();
							if(secondWord.isEmpty() && wordLength < 21 && backspace_present) {
								while(!firstWord.isEmpty())
									secondWord.push(firstWord.pop());
								if(backspace_present) {
									previous_word = true;
									backspace_present = false;
								}
							}
							if(firstWord.size() > 20 || secondWord.size() > 20) {
								firstWord.clear();
								secondWord.clear();
							}
							if (!firstWord.isEmpty() && !secondWord.isEmpty()) {
								if(backspace_present && previous_word) {
									//System.out.println(secondWord.size() + " " + firstWord.size() + " " + firstWord.peek().getWhen() + " " + firstWord.peek().getKeyChar() + " " + secondWord.peekLast().getWhen() + " " + secondWord.peekLast().getKeyChar() + " " + a.getAnswerID());
								featureMap.get(secondWord.size() * 100 + firstWord.size()).add(firstWord.peek().getWhen() - secondWord.peekLast().getWhen());
									secondWord.clear();
									while(!firstWord.isEmpty())
										secondWord.push(firstWord.pop());
									if(backspace_present) {
										previous_word = true;
										backspace_present = false;
									}
								}
							}
							else if(secondWord.isEmpty() && wordLength < 21 && !backspace_present)
								firstWord.clear();
						}
					
					if(k.isAlphaNumeric())
						firstWord.push(k);
					else if (k.isSpace() && firstWord.size() > 0)
						firstWord.push(k);
					else if (k.isBackspace() && firstWord.size() > 0) {
						firstWord.pop();
						backspace_present = true;
					}
				}
			}
		}
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (int i = 1; i < 21; i++) {
			for(int j = 1; j < 21; j++) {
				output.add(new Feature("PBWL_" + i + "_" + j, featureMap.get(i * 100 + j)));
			}
		}
		return output;
	}
	
	/**
	 * Creates the search space for the module.
	 */
	private void createSearchSpace() {
		featureMap.clear();
		for (int i = 1; i < 21; i++) {
			for(int j = 1; j < 21; j++) {
 				featureMap.put(i * 100 + j, new LinkedList<Long>());
			}
		}
	}
	
	/**
	 * deletes the spaces in the word.
	 */
	private int popSpaces() {
		while (firstWord.peek().isSpace() )
			firstWord.pop();
		return firstWord.size();
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
		return "Pause Between Words";
	}
}