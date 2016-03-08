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
public class HandSpeed implements ExtractionModule{
	private final Set<String> handNames = new HashSet<String>(Arrays.asList("Left_Hand","Right_Hand"));
	private Set<String> searchSpace = new TreeSet<String>();
	private HashMap<String, LinkedList<Long>> featureMap = new HashMap<String, LinkedList<Long>>();
	
	public HandSpeed() {
		searchSpace.clear();
		featureMap.clear();
		generateSearchSpace();
	}
	
	public void generateSearchSpace() {
		for (String hand : handNames)
			searchSpace.add(hand);
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (String s : searchSpace)
			featureMap.put(s, new LinkedList<Long>());
		
		for (Answer a : data) {
			Collection<KSE> kseArray = KSE.parseSessionToKSE(a.getKeyStrokes());
			for (KSE kse : kseArray) {
				if (kse.isKeyPress())
					if (searchSpace.contains(kse.kseGetHand()))
						featureMap.get(kse.kseGetHand()).add(kse.getM_pauseMs());
			}
		}
		
		//create output feature list
		LinkedList<Feature> output = new LinkedList<Feature>();
				
		//iterate over the featureMap using searchSpace because it is auto-sorted by TreeSet class.
		for (String s : searchSpace)
			output.add(new Feature("Speed_"+s, featureMap.get(s)));
		
		//calculate and normed ratio
		output.add(new Feature(this.getName()+"_NormedRatio",getNormedRatio(featureMap)));
		
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	//finds the average of each hand, and returns ratio of averages
	public double getNormedRatio(HashMap<String, LinkedList<Long>> featMap) {
		
		double normedRatio = 0.0;
		
		LinkedList<Long> leftHandList = featMap.get("Left_Hand");
		LinkedList<Long> rightHandList = featMap.get("Right_Hand");
		
		long leftHandSum = 0;
		long rightHandSum = 0;
		
		for (Long l : leftHandList) {
			leftHandSum += l;
		}
		
		for (long l : rightHandList) {
			rightHandSum += l;
		}
		
		//create average count, and divide left hand average by right hand average
		normedRatio = ((leftHandSum*1.)/leftHandList.size()) / ((rightHandSum*1.)/rightHandList.size());
		
		return normedRatio;
	}

	@Override
	public String getName() {
		return "Hand_Speed";
	}

}
