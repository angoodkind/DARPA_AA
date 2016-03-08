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
public class FeatureWordCount implements ExtractionModule {
	int bckSpcWordCount;
	boolean first;
	HashMap<Integer, LinkedList<Integer>> featureMap = new HashMap<Integer, LinkedList<Integer>>(20);
	LinkedList<KeyStroke> cs = new LinkedList<KeyStroke>();
	/**
	 * Runs When the Create Template Method is Clicked.
	 * 
	 * @see Answer
	 */
	public Collection<Feature> extract(DataNode data) {
		featureMap.clear();
		bckSpcWordCount = 0;
		first = false;
		for (int i = 1; i < data.getUserID()+1; i++)
			featureMap.put(i, new LinkedList<Integer>());
		cs.clear();
		//Iterates over all answers for a user.
		for (Answer a : data) {
			//Iterate over all the KeyStrokes in an Answer
			for (KeyStroke k : a.getKeyStrokeList()){
				if(k.getKeyChar()!=KeyStroke.CHAR_UNDEFINED && k.getKeyCode()!=32 && k.isKeyPress() && !(Character.toString(k.getKeyChar()).matches("\\p{Punct}"))){
					cs.push(k);
					if(cs.peek().getKeyCode() == 8)
						first = true;
				}
				else if(k.getKeyCode() == 32 && k.isKeyPress() && cs.size() > 0 && first){
					bckSpcWordCount++;
					first = false;
					cs.clear();
				}
			}
		}
		featureMap.get(data.getUserID()).add(bckSpcWordCount);
		//Returns output to the extractor.
		LinkedList<Feature> output = new LinkedList<Feature>();
		output.add(new Feature("BSWC_U" + data.getUserID(), featureMap.get(data.getUserID())));
		return output;
		}
	/**
	 * Returns the Module's Name.
	 */
	public String getName() {
		// A nicely formated String with the module's name.
		return "bckSpcCount1";
	}
}