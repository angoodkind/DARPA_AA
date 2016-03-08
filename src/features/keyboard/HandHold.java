
package features.keyboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import features.pause.KSE;
import keystroke.KeyStroke;
/**
 * @author agoodkind
 * Hand hold times for each of 2 hands
 */
public class HandHold implements ExtractionModule {

	private final Set<String> handNames = new HashSet<String>(Arrays.asList("Left_Hand","Right_Hand"));
	private Set<String> searchSpace = new TreeSet<String>();
	private HashMap<String, LinkedList<Long>> featureMap = new HashMap<String, LinkedList<Long>>();
	
	public HandHold() {
		searchSpace.clear();
		featureMap.clear();
		generateSearchSpace();
	}
	
	public void generateSearchSpace() {
		for (String hand : handNames)
			searchSpace.add(hand);
	}
	
	@Override
	public LinkedList<Feature> extract(DataNode data) {
		
		for (String s : searchSpace)											//populate FeatureMap
			featureMap.put(s, new LinkedList<Long>());
	
		Hashtable<Integer, LinkedList<Integer>> keyTable = constructKeyTable();	//create keyTable of all VK Codes
		TreeSet<Integer> keySet = new TreeSet<Integer>(keyTable.keySet());		//create keySet based on keyTable
		
		for (Answer a : data){
			Collection<KSE> kseArray = KSE.parseSessionToKSE(a.getKeyStrokes());// create array of KSEs
			
		for (Integer vkCode : keySet) {											// iterate through each VK Code
				long pressTime = 0;												// set pressTime to 0
					boolean firstKeyPress = true;								// set firstKeyPress	
					for (KSE kse : kseArray) {									// go through each KSE
						if (kse.getKeyCode() == vkCode) {						// if KSE's vk matches above vk
							if (kse.isKeyPress() && firstKeyPress) {			// if KSE is a keypress, and firstPress			
								pressTime = kse.getWhen();						// pressTime = time key was pressed
								firstKeyPress = false;							// set keypress to false
							}
							else if (kse.isKeyRelease() && pressTime != 0) {
//							keyTable.get(vkCode).add((int)(kse.getWhen() - pressTime));
								if (searchSpace.contains(kse.kseGetHand())) {
//								System.out.println(kse.getKeyChar()+" "+kse.getFinger()+"\t"+featureMap.get(kse.getFinger()));
									featureMap.get(kse.kseGetHand()).add(kse.getWhen() - pressTime);
								}
								firstKeyPress = true;
							}
						}
					}
				}	
		}
		
		//create output feature list
		LinkedList<Feature> output = new LinkedList<Feature>();
				
		//iterate over the featureMap using searchSpace because it is auto-sorted by TreeSet class.
		for (String s : searchSpace)
			output.add(new Feature("Hold_"+s, featureMap.get(s)));
			//calculate and normed ratio
			output.add(new Feature(this.getName()+"_NormedRatio",getNormedRatio(featureMap)));
		
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	/**
	 * Performs setup operations on the hashtable to be used during extraction of values.
	 * 
	 * @return Hashtable to be used during extraction.
	 */
	private Hashtable<Integer, LinkedList<Integer>> constructKeyTable(){
		Hashtable<Integer, LinkedList<Integer>> keyTable = new Hashtable<Integer, LinkedList<Integer>>();
		for (Integer key : KeyStroke.UsefulVKCodes())
			keyTable.put(key, new LinkedList<Integer>());
		return keyTable;
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
		return "Hand_Hold";
	}

}
