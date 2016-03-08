package features.demographics;

import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class CogLoad6Way implements ExtractionModule {

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		//Defines a new empty Feature
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		for (Answer a : data) {
			int answerCogLoad = a.getCogLoad();
			output.add(new Feature("CogLoad6Way","CL"+answerCogLoad));
		}
		return output;
	}

	@Override
	public String getName() {
		return "CogLoad6Way";
	}

}
