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

// These are imported in order to interact with the extractor.
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

/**
 * @author Raviteja Pokala
 *
 * Example Module 
 */
public class FeatureSurvey implements ExtractionModule {
	int featureCount;
	HashMap<Integer, LinkedList<Integer>> featureMap = new HashMap<Integer, LinkedList<Integer>>(100);
	LinkedList<KeyStroke> wordStack = new LinkedList<KeyStroke>();
	Collection<KeyStroke> keys = null; 
	
	/**
	 * Runs When the Create Template Method is Clicked.
	 * 
	 * @see Answer
	 */
	public Collection<Feature> extract(DataNode data) {
		createSearchSpace();
		
		//Iterates over all answers for a user.
		for (Answer a : data) {
			wordStack.clear();
			featureCount = 0;
			keys = a.getKeyStrokeList();
			for (KeyStroke k : keys) {
				if(!k.isSpace() && k.isKeyPress() && k.isAlphaNumeric()) {
					wordStack.push(k);
						if(wordStack.peek().getKeyCode() == 8 ||wordStack.peek().getKeyCode() == 127) {
							featureCount++;
						}
				}
				else if(k.isSpace() && k.isKeyPress() && wordStack.size() > 0 && featureCount>0) {
					int size = wordStack.size() - (featureCount * 2);
					if(size>0) {
						featureMap.get(size * 100 + featureCount).add(1);
						featureCount = 0;
						wordStack.clear();
					}
				}
				else if(k.isSpace() && k.isKeyPress() && featureCount < 1 && wordStack.size() > 0 || wordStack.size() > 20) {
					wordStack.clear();
					featureCount = 0;
				}
				else if(wordStack.size() == 0)
					wordStack.clear();
			}
		}
		LinkedList<Feature> output = new LinkedList<Feature>();
		for (int i = 1; i < 20; i++) {
			for(int j = 1; j < 20; j++) {
				output.add(new Feature("BSC_L" + i + "_" + j, featureMap.get(i * 100 + j)));
			}
		}
		return output;
	}
	
	private void createSearchSpace() {
		featureMap.clear();
		for (int i = 1; i < 20; i++) {
			for(int j = 1; j < 20; j++) {
 				featureMap.put(i * 100 + j, new LinkedList<Integer>());
			}
		}
	}
	
	/**
	 * Returns the Module's Name.
	 */
	public String getName() {
		// A nicely formated String with the module's name.
		return "Feature Survey";
	}
}