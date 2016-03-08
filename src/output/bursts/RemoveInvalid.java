package output.bursts;

import java.util.LinkedList;

import extractors.data.Feature;

public class RemoveInvalid {
	
	public static void RemoveInfNaN(Feature feature) {
		
		/*
		 * This method remove NaN (Not a Number) and Infinity values from the 
		 * given feature.
		 *  
		 */		
		LinkedList<Object> values = (LinkedList<Object>) feature.getFeatureValues();
		
		for (int g = 0; g < values.size(); g++) {
			
			Double double_value = null;
			Object obj_val = values.get(g);
			
			if (obj_val.getClass().equals(Integer.class)) {
				Integer value = (Integer)obj_val;
				double_value = Double.valueOf(value.doubleValue());
			}
			
			if (obj_val.getClass().equals(Long.class)) {
				Long value = (Long)obj_val;
				double_value = Double.valueOf(value.doubleValue());
			}
			
			if (obj_val.getClass().equals(Double.class)) {
				Double value = (Double)obj_val;
				double_value = value;
			}
			
			if (double_value == null) {
				break;
			}
			else {
				if (double_value.isInfinite() || double_value.isNaN()) {
					//values.remove(g);
					//values.set(g, 0);
					values.set(g, -1);
				}
			}
		}
		feature.setFeatureValues(values);
	}

}
