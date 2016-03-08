package features.demographics;

import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class CogLoad3Way implements ExtractionModule {

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		//Defines a new empty Feature
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		for (Answer a : data) {
			int rawCogLoad = a.getCogLoad();
			String featureCogLoad;
			if (rawCogLoad <= 2)
				featureCogLoad = "CogLoad_12";
			else if (rawCogLoad <= 4)
				featureCogLoad = "CogLoad_34";
			else
				featureCogLoad = "CogLoad_56";
			output.add(new Feature("CogLoad3Way",featureCogLoad));
		}
		return output;
	}

	@Override
	public String getName() {
		return "CogLoad3Way";
	}

}
