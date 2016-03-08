/**
 * 
 */
package features.keyboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;

/**
 * @author agoodkind
 * Generate a list of lag times before each of 5 fingers
 */
public class FingerSpeed implements ExtractionModule{
	private final Set<String> fingerNames = new HashSet<String>(Arrays.asList("Pinky_Finger","Ring_Finger","Middle_Finger","Index_Finger","Thumb_Finger"));
	private Set<String> searchSpace = new TreeSet<String>();
	private HashMap<String, LinkedList<Long>> featureMap = new HashMap<String, LinkedList<Long>>();
	
	public FingerSpeed() {
		searchSpace.clear();
		featureMap.clear();
		generateSearchSpace();
	}
	
	public void generateSearchSpace() {
		for (String finger : fingerNames)
			searchSpace.add(finger);
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (String s : searchSpace) 					// create feature
			featureMap.put(s, new LinkedList<Long>());	// add a LinkedList for each String in searchSpace
		
		for (Answer a : data) {

			Collection<KSE> kseArray = KSE.parseSessionToKSE(a.getKeyStrokes());	// create KSE array from KeyStrokes 
			for (KSE kse : kseArray) {
				if (kse.isKeyPress())												// only look at KeyPresses
					if (searchSpace.contains(kse.kseGetFinger()))
						featureMap.get(kse.kseGetFinger()).add(kse.getM_pauseMs());	// add to Feature Map
			}
		}
		
		//create output feature list
		LinkedList<Feature> output = new LinkedList<Feature>();
				
		//iterate over the featureMap using searchSpace because it is auto-sorted by TreeSet class.
		for (String s : searchSpace)
			output.add(new Feature("Speed_"+s, featureMap.get(s)));
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	@Override
	public String getName() {
		return "Finger_Speed";
	}

}
