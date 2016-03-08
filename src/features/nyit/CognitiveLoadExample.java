package features.nyit;

import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

public class CognitiveLoadExample implements ExtractionModule {

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		//Defines a new empty Feature
		Feature cogLoad = new Feature("CogLoad");
				
		//iterate over all Answers (in case multiple Answers contained in data) 
		for (Answer a : data) {
			//gets cogload class information and adds to the feature's value list.
			cogLoad.add("COGLOAD_" +  a.getCogLoad() );
		}
		
		//constructs list of features for output
		LinkedList<Feature> output = new LinkedList<Feature>();
		//adds our one feature
		output.add(cogLoad);
		
		//return output to the 
		return output;
	}

	@Override
	public String getName() {
		return "Example Cognitive Load Class Fetcher";
	}

}
