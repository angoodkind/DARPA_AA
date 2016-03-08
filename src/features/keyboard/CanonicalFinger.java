/**
 * 
 */
package features.keyboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import extractors.data.Feature;
import keystroke.KeyStroke;


/**
 * @author agoodkind
 * methods and sources for keyboard layout processing 
 */
public abstract class CanonicalFinger {
	public final static Set<String> handNames = new HashSet<String>(Arrays.asList("Left_Hand","Right_Hand"));
	public final static Set<String> fingerNames = new HashSet<String>(Arrays.asList("Pinky_Finger","Ring_Finger","Middle_Finger","Index_Finger","Thumb_Finger"));
	public final static Set<String> rowNames = new HashSet<String>(Arrays.asList("Number_Row","Top_Row","Home_Row","Bottom_Row","Space_Row"));
	
	/**
	 * create a nested map for hands and either rows or fingers
	 * @param nestedMap - entire map
	 * @param outerMap - hand or row map
	 * @return a prepopulated nested map, with all key names
	 * Map looks like: {Thumb={Left = <1,2,3>, Right = <4,5,6>}, Pinkie={Left = <1,2,3>, Right = <4,5,6>}}
	 * 	- necessary for subsequent normalization processing
	 */
	public static LinkedHashMap<String, LinkedHashMap<String,LinkedList<Long>>> createNestedMap 
	(LinkedHashMap<String, LinkedHashMap<String,LinkedList<Long>>> nestedMap, Set<String> outerMap) {
		//for each outer/hand combination
		for (String outerItem : outerMap)
			for (String hand : handNames) {
				// create a new inner-hand map
				LinkedHashMap<String, LinkedList<Long>> handMap = new LinkedHashMap<String, LinkedList<Long>>();
				//  add key and blank list to inner map
				handMap.put(hand, new LinkedList<Long>());
				// if this is not the initial hand being added, add map to existing finger
				if (nestedMap.get(outerItem) != null)
					nestedMap.get(outerItem).put(hand, new LinkedList<Long>());
				// if this is the first handMap to be added to the finger, create aeew
				else
					nestedMap.put(outerItem, handMap);
			}
		return nestedMap;
	}
	
	// add normed speeds, using nested map
	public static void addNormedToOutput(Set<String> outerNames,Collection<Feature> output,
			LinkedHashMap<String, LinkedHashMap<String,LinkedList<Long>>> nestedMap, String holdOrSpeed) {
		for (String outerItem : outerNames) {
			LinkedHashMap<String,LinkedList<Long>> entry = nestedMap.get(outerItem);
			output.add(new Feature("Normed_"+holdOrSpeed+"_"+outerItem,getNormedRatio(entry)));
			
//			output.add(new Feature("Normed_TopQuint_"+outerItem,getNormedQuint(entry)));
			
			entry.clear();
		}
	}
	
//	// reduces map to top quintile, and returns normed ratio from getNormedRatio()
//	public static double getNormedQuint(LinkedHashMap<String, LinkedList<Long>> featMap) {
//		double normedRatio = 0.0;
//		
//		LinkedList<Long> leftHandList = featMap.get("Left_Hand");
//		LinkedList<Long> rightHandList = featMap.get("Right_Hand");
//		
//		long leftHandSum = 0, rightHandSum = 0;
//		int leftHandKSs = 0, rightHandKSs = 0;
//		ArrayList<Long> leftHandSorted = new ArrayList<Long>(), rightHandSorted = new ArrayList<Long>();
//		
//		
//		for (Long leftHandTime : leftHandList) {
//			leftHandSum += leftHandTime;
//			leftHandSorted.add(leftHandTime);
//		}
//		
//		
//		for (long rightHandTime : rightHandList) 
//			rightHandSum += rightHandTime;
//	}

	
	//finds the average of each hand, and returns ratio of averages
	public static double getNormedRatio(LinkedHashMap<String, LinkedList<Long>> featMap) {
		
		double normedRatio = 0.0;
		
		LinkedList<Long> leftHandList = featMap.get("Left_Hand");
		LinkedList<Long> rightHandList = featMap.get("Right_Hand");
		
		long leftHandSum = 0;
		long rightHandSum = 0;
		
		for (Long leftHandTime : leftHandList) {
			leftHandSum += leftHandTime;
		}
		
		for (long rightHandTime : rightHandList) {
			rightHandSum += rightHandTime;
		}
		
		//create average count, and divide left hand average by right hand average
		normedRatio = ((leftHandSum*1.)/leftHandList.size()) / ((rightHandSum*1.)/rightHandList.size());
		
		return normedRatio;
	}
	
	
	///// Single List Booleans /////
	
	public boolean isLeftHand(KeyStroke k) {
		if (leftHandKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isRightHand(KeyStroke k) {
		if (rightHandKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isPinkyFinger(KeyStroke k) {
		if (pinkyFingerKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isRingFinger(KeyStroke k) {
		if (ringFingerKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isMiddleFinger(KeyStroke k) {
		if (middleFingerKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isIndexFinger(KeyStroke k) {
		if (indexFingerKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isThumb(KeyStroke k) {
		if (thumbKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isNumberRow(KeyStroke k) {
		if (numberRowKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isTopRow(KeyStroke k) {
		if (topRowKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isHomeRow(KeyStroke k) {
		if (homeRowKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isBottomRow(KeyStroke k) {
		if (bottomRowKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	public boolean isSpaceRow(KeyStroke k) {
		if (spaceRowKeys.contains(k.getKeyCode()))
			return true;
		return false;
	}
	
	/****Integer Equivalents****/
	//hand
	private Set<Integer> leftHandKeys = new HashSet<Integer>(Arrays.asList(9,17,18,20,49,50,51,52,53,65,66,67,68,69,70,
			71,81,82,83,84,86,87,88,90,192));
	private Set<Integer> rightHandKeys = new HashSet<Integer>(Arrays.asList(8,10,27,37,38,39,40,44,45,46,47,48,54,55,56,
			57,59,61,72,73,74,75,76,77,78,79,80,85,89,91,92,93,222));
	// Finger
	private Set<Integer> pinkyFingerKeys = new HashSet<Integer>(Arrays.asList(8,9,10,16,17,18,20,27,37,38,39,40,45,47,48,49,59,
			61,65,80,81,90,91,92,93,192,222));
	private Set<Integer> ringFingerKeys = new HashSet<Integer>(Arrays.asList(46,50,57,70,76,79,83,87,88));
	private Set<Integer> middleFingerKeys = new HashSet<Integer>(Arrays.asList(44,51,56,67,68,69,73,75));
	private Set<Integer> indexFingerKeys = new HashSet<Integer>(Arrays.asList(52,53,54,55,66,71,72,74,77,78,82,84,85,86,89));
	private Set<Integer> thumbKeys = new HashSet<Integer>(Arrays.asList(32));
	// Row
	private Set<Integer> numberRowKeys = new HashSet<Integer>(Arrays.asList(8,27,45,48,49,50,51,52,53,54,55,56,57,61,192));
	private Set<Integer> topRowKeys = new HashSet<Integer>(Arrays.asList(9,69,73,79,80,81,82,84,85,87,89,91,92,93));
	private Set<Integer> homeRowKeys = new HashSet<Integer>(Arrays.asList(10,20,59,65,68,70,71,72,74,75,76,83,222));
	private Set<Integer> bottomRowKeys = new HashSet<Integer>(Arrays.asList(16,44,46,47,66,67,77,78,86,88,90));
	private Set<Integer> spaceRowKeys = new HashSet<Integer>(Arrays.asList(17,18,32,37,38,39,40));

	
}
