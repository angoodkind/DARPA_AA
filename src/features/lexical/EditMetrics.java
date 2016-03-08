package features.lexical;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.POS_Extractor;

public class EditMetrics extends POS_Extractor implements ExtractionModule {
	
	protected final char BACKSPACE_KEY = 8;
	protected final char DELETE_KEY = 127;

	@Override
	public Collection<Feature> extract(DataNode data) {

	    // to hold list of ints of the number of times the "delete" key was pressed
	    Collection<Long> deletes = new LinkedList<>();

	    // to hold all of the POS_Pause features
	    LinkedList<Feature> output = new LinkedList<Feature>();

	    for (Answer a : data) {
	      try {
	        // get List of word Tokens
	        String[] tokens = runTokenizer(a.getCharStream());
	        
	        for (String s : tokens) {
	        	deletes.add(countDeletes(s));
	        }

	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
	    
	    output.add(new Feature("DeletesPerformed", deletes));

//	    for (Feature f : output)
//	      System.out.println(f.toTemplate());

	    return output;
	}
	
	public long countDeletes(String s) {
		long deletes = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == BACKSPACE_KEY || s.charAt(i) == DELETE_KEY)
				++deletes;
		}
		return deletes;
	}

	@Override
	public String getName() {
		return "EditMetrics";
	}

}
