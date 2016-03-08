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
 * Generate a list of lag times before 10 combinations of hand and row
 * 	exceptions:
 * 		space bar
 * 		shift keys
 */
public class RowHandSpeed implements ExtractionModule{
	private final Set<String> rowNames = new HashSet<String>(Arrays.asList("Number_Row","Top_Row","Home_Row","Bottom_Row","Space_Row"));
	private final Set<String> handNames = new HashSet<String>(Arrays.asList("Left_Hand","Right_Hand"));
	private Set<String> searchSpace = new TreeSet<String>();
	private HashMap<String, LinkedList<Long>> featureMap = new HashMap<String, LinkedList<Long>>();
	private LinkedHashMap<String, LinkedHashMap<String,LinkedList<Long>>> nestedHandMap = new LinkedHashMap<String, LinkedHashMap<String,LinkedList<Long>>>();
	
	public RowHandSpeed() {
		searchSpace.clear();
		featureMap.clear();
		nestedHandMap.clear();
		generateSearchSpace();
	}
	
	public void generateSearchSpace() {
		for (String row : rowNames)
			for (String hand : handNames)
				searchSpace.add(hand+"_"+row);
//		searchSpace.add("None_Space_Row");
//		searchSpace.add("None_Bottom_Row");
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		nestedHandMap = CanonicalFinger.createNestedMap(nestedHandMap,rowNames);

		for (String s : searchSpace)
			featureMap.put(s, new LinkedList<Long>());
		
		for (Answer a : data) {
			Collection<KSE> kseArray = KSE.parseSessionToKSE(a.getKeyStrokes());
			for (KSE kse : kseArray) {
				if (kse.isKeyPress())
					if (searchSpace.contains(kse.kseGetHand()+"_"+kse.kseGetRow())) {
						featureMap.get(kse.kseGetHand()+"_"+kse.kseGetRow()).add(kse.getM_pauseMs());
						// append to proper inner map
						if (nestedHandMap.get(kse.kseGetRow()).get(kse.kseGetHand()) != null) {
							LinkedList<Long> list = nestedHandMap.get(kse.kseGetRow()).get(kse.kseGetHand());
							list.add(kse.getM_pauseMs());
						}
					}
			}
		}
		
		//create output feature list
		LinkedList<Feature> output = new LinkedList<Feature>();
				
		//iterate over the featureMap using searchSpace because it is auto-sorted by TreeSet class.
		for (String s : searchSpace)
			output.add(new Feature("Speed_"+s, featureMap.get(s)));
		// add normed speeds, using nested map
		CanonicalFinger.addNormedToOutput(rowNames, output, nestedHandMap, "Speed");
//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	@Override
	public String getName() {
		return "Row-Hand_Speed";
	}

}
