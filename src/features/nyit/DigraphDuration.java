package features.nyit;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;


/**
 * Calculates Digraph Durations
 * 
 * @author Patrick
 */
public class DigraphDuration implements ExtractionModule {
	
	private Set<String> searchSpace = new TreeSet<String>();
	private HashMap<String, LinkedList<Long>> featureMap = new HashMap<String, LinkedList<Long>>(1000);
	
	/**
	 * Initialize the module.
	 */
	public DigraphDuration () {
		featureMap.clear();
		searchSpace.clear();
		generateSearchSpace();
	}
	
	/**
	 * Generates full permutation of two UpperCase alpha combinations and places
	 * them into a TreeSet which ensures no duplicates and automatically sorts them!
	 */
	private void generateSearchSpace() {
		for (char a = 65; a < 91; a++)
			for (char b = 65; b < 91; b++)
				searchSpace.add("" + a + b);
	}
		
	
	
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		//clear feature map and repopulate it's keys for a new extraction
		featureMap.clear();
		for (String s : searchSpace)
			featureMap.put(s, new LinkedList<Long>());
		
		//create a buffer to hold the keystrokes during processing
		LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
		//String digraph used to hold d
		String digraph;
		for (Answer a : data) {
			for (KeyStroke k : a.getKeyStrokeList()) {
			/////////////////Main Logic///////////////////////
				
				if (k.isKeyPress()) {									//only look at keypresses
					buffer.add(k);										//add a keypress to the buffer
					if (buffer.size() > 2)								//if the buffer contains more than 2 keypresses
						buffer.poll();									//remove the first keystroke in the buffer
					if (buffer.size() == 2) {							//if buffer has 2 keystrokes in it
						digraph = (KeyStroke.keyStrokesToString(buffer) //convert it to a textual representation
								.toUpperCase()); 						//and make it uppercase
						if (searchSpace.contains(digraph))				//if the digraph is in the searchSpace Set
								featureMap.get(digraph)					//get the digraph's entry in the Map
										.add(buffer.getLast().getWhen() //add the latency between the keystrokes... 
										- buffer.getFirst().getWhen()); //to the list contained in the Map.
					}
				}
			/////////////////////////////////////////////////
			}
		}
		
		//create output feature list.
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		//iterate over the featureMap using searchSpace because it is auto-sorted by TreeSet class.
		for (String s : searchSpace)
			output.add(new Feature("DN_" + s, featureMap.get(s)));
		return output;
	}

	@Override
	public String getName() {
		return "Digraph Duration";
	}
	
}
