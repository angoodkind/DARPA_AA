/**
 * 
 */
package features.demographics;

import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;

/**
 * @author agoodkind
 * pulls info such as Subj_Id and Answer_Id
 */
public class SubjectID implements ExtractionModule {
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		Feature subjAnswID = new Feature("SubjAnswID");
		
		for (Answer a : data ) {
			subjAnswID.add("S"+data.getUserID()+" A"+a.getAnswerID()+" CL"+a.getCogLoad());
//			System.out.println("S"+data.getUserID()+" A"+a.getAnswerID()+" CL"+a.getCogLoad());
		}
		
		//constructs list of features for output
		LinkedList<Feature> output = new LinkedList<Feature>();
		//adds our one feature
		output.add(subjAnswID);
		
		return output;
	}

	@Override
	public String getName() {
		return "Subject Identification";
	}

}
