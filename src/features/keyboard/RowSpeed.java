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
public class RowSpeed implements ExtractionModule{
	private final Set<String> rowNames = new HashSet<String>(Arrays.asList("Number_Row","Top_Row","Home_Row","Bottom_Row","Space_Row"));
	private Set<String> searchSpace = new TreeSet<String>();
	private HashMap<String, LinkedList<Long>> featureMap = new HashMap<String, LinkedList<Long>>();
	
	public RowSpeed() {
		searchSpace.clear();
		featureMap.clear();
		generateSearchSpace();
	}
	
	public void generateSearchSpace() {
		for (String row : rowNames)
			searchSpace.add(row);
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (String s : searchSpace)
			featureMap.put(s, new LinkedList<Long>());
		
		for (Answer a : data) {
			Collection<KSE> kseArray = KSE.parseSessionToKSE(a.getKeyStrokes());
			for (KSE kse : kseArray) {
				if (kse.isKeyPress())
					if (searchSpace.contains(kse.kseGetRow()))
						featureMap.get(kse.kseGetRow()).add(kse.getM_pauseMs());
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
		return "Row_Speed";
	}

}
