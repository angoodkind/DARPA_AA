package output.util;

import java.util.LinkedList;

/**
 * @author Sathya (sgovin03@nyit.edu)
 *	This is a helper class that converts Object array with unknown datatype to array of Double, Integer, Long.
 *	This comes in handy when there is a need for further processing of the feature values.
 */

public class ConvertGenerics {
	
	private String original_type = null;
	private Object[] original_values = null;
	
	public ConvertGenerics (LinkedList<Object> values) {
		original_type = values.get(0).getClass().getCanonicalName();
		original_values = values.toArray();
		//System.out.println("Original Data-type: " + original_type);
	}
	
	public Double[] ToDoubleArray() {
		
		Double[] double_array = new Double[original_values.length];
		
		for (int g = 0; g < original_values.length; g++) {
			
			Object obj_val = original_values[g];
			
			switch (original_type) {
			
				case "java.lang.Integer": {
					Integer value = (Integer)obj_val;
					double_array[g] = Double.valueOf(value.doubleValue());
					break;
				}
				
				case "java.lang.Long": {
					Long value = (Long)obj_val;
					double_array[g] = Double.valueOf(value.doubleValue());
					break;
				}
				
				case "java.lang.Double": {
					Double value = (Double)obj_val;
					double_array[g] = value;
					break;
				}
				
			}
		}
		
		return double_array;
	}
	
	public Integer[] ToIntegerArray() {
		
		Integer[] Int_array = new Integer[original_values.length];
		
		for (int g = 0; g < original_values.length; g++) {
			
			Object obj_val = original_values[g];
			
			switch (original_type) {
			
				case "java.lang.Integer": {
					Integer value = (Integer)obj_val;
					Int_array[g] = value;
					break;
				}
				
				case "java.lang.Long": {
					Long value = (Long)obj_val;
					Int_array[g] = Integer.valueOf(value.intValue());
					break;
				}
				
				case "java.lang.Double": {
					Double value = (Double)obj_val;
					Int_array[g] = Integer.valueOf(value.intValue());
					break;
				}
				
			}
		}
		
		return Int_array;
	}
	
	public Long[] ToLongArray() {
		
		Long[] Long_array = new Long[original_values.length];
		
		for (int g = 0; g < original_values.length; g++) {
			
			Object obj_val = original_values[g];
			
			switch (original_type) {
			
				case "java.lang.Integer": {
					Integer value = (Integer)obj_val;
					Long_array[g] = Long.valueOf(value.longValue());
					break;
				}
				
				case "java.lang.Long": {
					Long value = (Long)obj_val;
					Long_array[g] = value;
					break;
				}
				
				case "java.lang.Double": {
					Double value = (Double)obj_val;
					Long_array[g] = Long.valueOf(value.intValue());
					break;
				}
				
			}
		}
		
		return Long_array;
	}

}