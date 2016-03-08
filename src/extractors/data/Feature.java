package extractors.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Feature {

	private String featureName;
	private LinkedList<Object> featureValues;
	
	public Feature() {
		this.featureName = null;
		this.featureValues = null;
	}
	
	public boolean isEmpty() {
		return featureValues.isEmpty();
	}
	
	public Feature(String featureName) {
		this.featureName = featureName;
		this.featureValues = new LinkedList();
	}
	
	public Feature(String featureName, Collection featureValues) {
		this.featureName = featureName;
		this.featureValues = new LinkedList(featureValues);
	}
	
	public Feature(String featureName, Object... featureValues) {
		this.featureName = featureName;
		this.featureValues = new LinkedList(Arrays.asList(featureValues));
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public LinkedList getFeatureValues() {
		return featureValues;
	}

	public void setFeatureValues(Collection featureValues) {
		this.featureValues = new LinkedList(featureValues);
	}
	
	public void add(Object featureValue) {
		this.featureValues.add(featureValue);
	}
	
	public String toVector() {
		String values = new String("");
		for (Object o : this.featureValues)
			try {
			values += o.toString() + ",";
			} catch (NullPointerException e) {
				System.err.println("Null Feature Value!");
			}
		if (values.length() > 0)
			return values.substring(0, values.length()-1);
		else
			return values;
	}
	
	public String toTemplate() {
		return this.featureName + ": " + this.toVector();
	}
	
}
