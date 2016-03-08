package features.demographics;

import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class CogLoad2Way implements ExtractionModule {

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		//Defines a new empty Feature
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		for (Answer a : data) {
			int rawCogLoad = a.getCogLoad();
			String featureCogLoad;
			if (rawCogLoad <= 3)
				featureCogLoad = "CogLoad_123";
			else
				featureCogLoad = "CogLoad_456";
			output.add(new Feature("CogLoad2Way",featureCogLoad));
		}
		return output;
	}

	@Override
	public String getName() {
		return "CogLoad2Way";
	}

}
