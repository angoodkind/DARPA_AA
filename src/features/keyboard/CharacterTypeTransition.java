/**
 * 
 */
package features.keyboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

/**
 * @author agoodkind
 * Generate a list of lag times between different categories of keys
 *
 */
public class CharacterTypeTransition implements ExtractionModule {
	// set of unique categories for a keystroke
	private final TreeSet<String> categories = new TreeSet<String>(Arrays.asList("Consonant","Vowel","Space","Punctuation","Number","Backspace","Function"));
	private Set<String> searchSpace;
	private HashMap<String, LinkedList<Long>> featureMap;
	
	public CharacterTypeTransition() {
		searchSpace = new TreeSet<String>();
		featureMap = new HashMap<String, LinkedList<Long>>();;
	}
	
	public void clearLists() {
		searchSpace.clear();
		featureMap.clear();
	}
	
	public void generateSearchSpace() {
		for (String category1 : categories)
			for (String category2 : categories)
				searchSpace.add(category1+"_TO_"+category2);
		for (String s : searchSpace)
			featureMap.put(s, new LinkedList<Long>());
	}
		
	@Override
	public Collection<Feature> extract(DataNode data) {
		clearLists();
		generateSearchSpace();
		
		for (Answer a : data) {
			processCharacters(a.getKeyStrokeList());
		}
		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();
				
		//iterate over the featureMap using searchSpace because it is auto-sorted by TreeSet class.
		for (String s : searchSpace)
			output.add(new Feature(s, featureMap.get(s)));
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	public void processCharacters(EventList<KeyStroke> keystrokes) {
		//create a buffer to hold the keystrokes during processing
		LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
		//String used to hold transition type
		String transition_category;
		for (KeyStroke k : keystrokes) {
			if (k.isKeyPress()) {									//only look at keypresses
				buffer.add(k);										//add a keypress to the buffer
				if (buffer.size() > 2)								//if the buffer contains more than 2 keypresses
					buffer.poll();									//remove the first keystroke in the buffer
				if (buffer.size() == 2) {							//if buffer has 2 keystrokes in it
					transition_category = getTransitionType(buffer);
					if (searchSpace.contains(transition_category)) {
						featureMap.get(transition_category).add		// add the latency between the two keys
						(buffer.getLast().getWhen() - buffer.getFirst().getWhen()); 	// to the existing list
					}
				}
			}
		}

	}

	// determine the type of transition, by determining the category of each keystroke
	public String getTransitionType(LinkedList<KeyStroke> buffer) {
		String keystroke1Category = getKeystrokeCategory(buffer.get(0));
		String keystroke2Category = getKeystrokeCategory(buffer.get(1));
		String transitionType = keystroke1Category+"_TO_"+keystroke2Category;
//		System.out.println(buffer.get(0).getKeyChar()+" "+buffer.get(1).getKeyChar()+"\t"+transitionType+"\t"+
//				(buffer.getLast().getWhen() - buffer.getFirst().getWhen()));
		return transitionType;
	}
	
	//determine the category of a Keystroke
	public String getKeystrokeCategory(KeyStroke key) {
		if (key.isConsonant())
			return "Consonant";
		else if (key.isVowel())
			return "Vowel";
		else if (key.isNumeric())
			return "Number";
		else if (key.isSpace())
			return "Space";
		else if (key.isPunctuation())
			return "Punctuation";
		else if (key.isBackspace())
			return "Backspace";
		else if (!key.isVisible())
			return "Function";
		else
			return "No_Category";
	}
	
	public HashMap<String, LinkedList<Long>> getFeatureMap() {
		return featureMap;
	}
	
	@Override
	public String getName() {
		return "Char_Type_Transition";
	}

}
