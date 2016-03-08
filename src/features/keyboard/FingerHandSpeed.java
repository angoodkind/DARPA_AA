/**
 * 
 */
package features.keyboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
 * Generate a list of lag times before each of 10 unique fingers
 * Also returns normed ratio between hands
 */
public class FingerHandSpeed implements ExtractionModule{
	private final Set<String> fingerNames = new HashSet<String>(Arrays.asList("Pinky_Finger","Ring_Finger","Middle_Finger","Index_Finger","Thumb_Finger"));
	private final Set<String> handNames = new HashSet<String>(Arrays.asList("Left_Hand","Right_Hand"));
	private Set<String> searchSpace = new TreeSet<String>();
	private HashMap<String, LinkedList<Long>> featureMap = new HashMap<String, LinkedList<Long>>();
	private LinkedHashMap<String, LinkedHashMap<String,LinkedList<Long>>> nestedHandMap = new LinkedHashMap<String, LinkedHashMap<String,LinkedList<Long>>>();
	
	public FingerHandSpeed() {
		searchSpace.clear();
		featureMap.clear();
		nestedHandMap.clear();
		generateSearchSpace();
	}

	public void generateSearchSpace() {
		for (String finger : fingerNames)
			for (String hand : handNames)
				searchSpace.add(hand+"_"+finger);
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		nestedHandMap = CanonicalFinger.createNestedMap(nestedHandMap,fingerNames);
		
		for (String s : searchSpace)
			featureMap.put(s, new LinkedList<Long>());
		
		for (Answer a : data) {
			Collection<KSE> kseArray = KSE.parseSessionToKSE(a.getKeyStrokes());
			for (KSE kse : kseArray) {
				if (kse.isKeyPress())
					if (searchSpace.contains(kse.kseGetHand()+"_"+kse.kseGetFinger())) {
						featureMap.get(kse.kseGetHand()+"_"+kse.kseGetFinger()).add(kse.getM_pauseMs());
						// append to proper inner map
						LinkedList<Long> list = nestedHandMap.get(kse.kseGetFinger()).get(kse.kseGetHand());
						list.add(kse.getM_pauseMs());
					}
			}
		}
		
		//create output feature list
		LinkedList<Feature> output = new LinkedList<Feature>();
				
		//iterate over the featureMap using searchSpace because it is auto-sorted by TreeSet class.
		for (String s : searchSpace)
			output.add(new Feature("Speed_"+s, featureMap.get(s)));
		
		// add normed speeds, using nested map
		CanonicalFinger.addNormedToOutput(fingerNames, output, nestedHandMap, "Speed");
				
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	@Override
	public String getName() {
		return "Finger-Hand_Speed";
	}

}
