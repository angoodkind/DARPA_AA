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
public class WordCount implements ExtractionModule {
	int wordCount;
	HashMap<Integer, LinkedList<Integer>> featureMap = new HashMap<Integer, LinkedList<Integer>>(20);
	/**
	 * Runs When the Create Template Method is Clicked.
	 * 
	 * @see Answer
	 */
	public Collection<Feature> extract(DataNode data) {
		featureMap.clear();
		wordCount = 1;		
		for (int i = 1; i < data.getUserID()+1; i++)
			featureMap.put(i, new LinkedList<Integer>());		
		//Iterates over all answers for a user.		
		for (Answer a : data) {
			//Iterate over all the KeyStrokes in an Answer
			for (KeyStroke k : a.getKeyStrokeList()){
				if(k.getKeyCode()==32 && k.isKeyPress())
					wordCount++;
			}
		}
		featureMap.get(data.getUserID()).add(wordCount);
		//Returns output to the extractor.
		LinkedList<Feature> output = new LinkedList<Feature>();
		output.add(new Feature("WC_U" + data.getUserID(), featureMap.get(data.getUserID())));
		return output;
		}
	/**
	 * Returns the Module's Name.
	 */
	public String getName() {
		// A nicely formated String with the module's name.
		return "Word Count";
	}
}
