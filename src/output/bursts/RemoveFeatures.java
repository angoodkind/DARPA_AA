package output.bursts;

import java.util.*;

import extractors.data.Feature;

public class RemoveFeatures {
	
	/*
	 * This method removes features from the given list based on 
	 * the list of given names.
	 *  
	 */
	public static void Remove(List<Feature> feature_list, String[] remove_list) {
		
		for (int f = 0; f < feature_list.size(); f++) {
			Feature feature = feature_list.get(f);
			//System.out.println("Original Feature Name: " + feature.getFeatureName());
			for (int s = 0; s < remove_list.length; s++) {
				//System.out.println("List name: " + remove_list[s]);
				if (feature.getFeatureName().equals(remove_list[s])) {
					//System.out.println("Passed");
					feature_list.remove(f);
					f--; // Decrement the pointer to compensate the change of order due to removal.
					break;
				}
			}				
		}
	}
}
